package com.evandev.spicedcider.compat.voxyworldgen;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.voxyworldgen.DimensionStateAccessor;
import com.ethan.voxyworldgenv2.core.Config;
import com.ethan.voxyworldgenv2.core.PlayerTracker;
import com.ethan.voxyworldgenv2.mixin.ServerChunkCacheMixin;
import com.ethan.voxyworldgenv2.platform.Services;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class VoxyWorldGenCatchup {

    private static final TicketType<ChunkPos> LOD_SYNC_TICKET =
            TicketType.create("spicedcider_lod_sync", Comparator.comparingLong(ChunkPos::toLong), 200);

    private static final int CATCHUP_BATCH = 8;
    private static final int MAX_PENDING_LOADS = 8;

    private static final AtomicInteger pendingLoads = new AtomicInteger();

    private VoxyWorldGenCatchup() {
    }

    public static void reset() {
        pendingLoads.set(0);
    }

    public static void runCatchup(MinecraftServer server, Map<ResourceKey<Level>, ?> dimensionStates,
                                  List<ServerPlayer> players) {
        if (isWindingDown(server)) return;

        PlayerTracker tracker = PlayerTracker.getInstance();
        List<ServerPlayer> order = new ArrayList<>(players.size());
        for (ServerPlayer player : players) if (tracker.needsBackfill(player.getUUID())) order.add(player);
        for (ServerPlayer player : players) if (!tracker.needsBackfill(player.getUUID())) order.add(player);

        boolean loadFromDisk = SpicedCiderConfig.commonOr(
                SpicedCiderConfig.COMMON.voxyWorldGenSyncUnloadedChunks, true);

        for (ServerPlayer player : order) {
            UUID uuid = player.getUUID();
            if (!tracker.isModded(uuid)) continue;

            LongSet synced = tracker.getSyncedChunks(uuid);
            if (synced == null) continue;
            if (!(player.level() instanceof ServerLevel playerLevel)) continue;
            if (!(dimensionStates.get(playerLevel.dimension()) instanceof DimensionStateAccessor state)) continue;

            int radius = state.isTellusActive()
                    ? Math.max(Config.DATA.generationRadius, 128)
                    : Config.DATA.generationRadius;

            List<ChunkPos> batch = new ArrayList<>();
            state.getDistanceGraph().collectCompletedInRange(
                    player.chunkPosition(), radius, synced, batch, CATCHUP_BATCH);
            if (batch.isEmpty()) {
                tracker.clearBackfill(uuid);
                continue;
            }

            for (ChunkPos pos : batch) synced.add(pos.toLong());

            ServerLevel level = state.getLevel();
            server.execute(() -> sendBatch(server, level, uuid, batch, loadFromDisk));
        }
    }

    private static void sendBatch(MinecraftServer server, ServerLevel level, UUID uuid,
                                  List<ChunkPos> batch, boolean loadFromDisk) {
        if (isWindingDown(server)) {
            unmark(uuid, batch);
            return;
        }

        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
        if (player == null) {
            unmark(uuid, batch);
            return;
        }

        ServerChunkCache cache = level.getChunkSource();
        for (ChunkPos pos : batch) {
            LevelChunk chunk = cache.getChunkNow(pos.x, pos.z);
            if (chunk != null) {
                Services.NETWORK.sendLODData(player, chunk);
            } else if (!loadFromDisk || !requestLoad(server, level, cache, uuid, pos)) {
                unmark(uuid, pos);
            }
        }
    }

    private static boolean requestLoad(MinecraftServer server, ServerLevel level, ServerChunkCache cache,
                                       UUID uuid, ChunkPos pos) {
        if (pendingLoads.get() >= MAX_PENDING_LOADS) return false;

        pendingLoads.incrementAndGet();
        CompletableFuture<ChunkResult<ChunkAccess>> future;
        try {
            cache.addRegionTicket(LOD_SYNC_TICKET, pos, 0, pos);
            future = ((ServerChunkCacheMixin) cache)
                    .invokeGetChunkFutureMainThread(pos.x, pos.z, ChunkStatus.FULL, true);
        } catch (Throwable t) {
            cache.removeRegionTicket(LOD_SYNC_TICKET, pos, 0, pos);
            pendingLoads.decrementAndGet();
            return false;
        }

        future.whenComplete((result, error) -> {
            pendingLoads.decrementAndGet();
            if (isWindingDown(server)) return;
            server.execute(() -> deliver(server, level, uuid, pos));
        });
        return true;
    }

    private static void deliver(MinecraftServer server, ServerLevel level, UUID requester, ChunkPos pos) {
        ServerChunkCache cache = level.getChunkSource();
        LevelChunk chunk = cache.getChunkNow(pos.x, pos.z);
        cache.removeRegionTicket(LOD_SYNC_TICKET, pos, 0, pos);
        if (chunk == null) {
            unmark(requester, pos);
            return;
        }

        PlayerTracker tracker = PlayerTracker.getInstance();
        long key = pos.toLong();
        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            if (!tracker.isModded(uuid)) continue;
            if (!uuid.equals(requester) && tracker.isSynced(uuid, key)) continue;
            Services.NETWORK.sendLODData(player, chunk);
        }
    }

    private static boolean isWindingDown(MinecraftServer server) {
        return server == null || !server.isRunning() || server.isStopped();
    }

    private static void unmark(UUID uuid, List<ChunkPos> batch) {
        LongSet synced = PlayerTracker.getInstance().getSyncedChunks(uuid);
        if (synced == null) return;
        for (ChunkPos pos : batch) synced.remove(pos.toLong());
    }

    private static void unmark(UUID uuid, ChunkPos pos) {
        LongSet synced = PlayerTracker.getInstance().getSyncedChunks(uuid);
        if (synced != null) synced.remove(pos.toLong());
    }
}
