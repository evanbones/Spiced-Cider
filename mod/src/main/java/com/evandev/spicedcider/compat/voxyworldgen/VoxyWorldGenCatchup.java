package com.evandev.spicedcider.compat.voxyworldgen;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.voxyworldgen.DimensionStateAccessor;
import com.ethan.voxyworldgenv2.core.Config;
import com.ethan.voxyworldgenv2.core.PlayerTracker;
import com.ethan.voxyworldgenv2.mixin.ServerChunkCacheMixin;
import com.ethan.voxyworldgenv2.network.NetworkHandler;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class VoxyWorldGenCatchup {

    private static final TicketType<ChunkPos> LOD_SYNC_TICKET =
            TicketType.create("spicedcider_lod_sync", Comparator.comparingLong(ChunkPos::toLong), 200);

    private static final int CATCHUP_BATCH = 8;
    private static final int MAX_PENDING_LOADS = 2;

    private static final AtomicInteger pendingLoads = new AtomicInteger();
    private static final Map<UUID, Binding> bindings = new ConcurrentHashMap<>();
    private static final Set<UUID> pendingBinds = ConcurrentHashMap.newKeySet();

    private record Binding(LongSet synced, ServerLevel level, LodSyncedChunks data) {
    }

    private VoxyWorldGenCatchup() {
    }

    public static void reset() {
        pendingLoads.set(0);
        detachAll();
    }

    public static void detachAll() {
        bindings.keySet().forEach(VoxyWorldGenCatchup::detach);
        pendingBinds.clear();
    }

    public static void detach(UUID uuid) {
        Binding binding = bindings.remove(uuid);
        if (binding != null) binding.data().detach(uuid, binding.synced());
    }

    private static boolean bound(MinecraftServer server, UUID uuid, LongSet synced, ServerLevel level) {
        Binding binding = bindings.get(uuid);
        if (binding != null && binding.synced() == synced && binding.level() == level) return true;
        if (pendingBinds.add(uuid)) server.execute(() -> bind(server, uuid, level));
        return false;
    }

    private static void bind(MinecraftServer server, UUID uuid, ServerLevel level) {
        try {
            if (isWindingDown(server)) return;
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            LongSet synced = PlayerTracker.getInstance().getSyncedChunks(uuid);
            if (player == null || synced == null || player.level() != level) return;
            detach(uuid);
            LodSyncedChunks data = LodSyncedChunks.get(level);
            data.attach(uuid, synced);
            bindings.put(uuid, new Binding(synced, level, data));
        } finally {
            pendingBinds.remove(uuid);
        }
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
            if (!bound(server, uuid, synced, playerLevel)) continue;

            int radius = state.isTellusActive()
                    ? Math.max(Config.DATA.generationRadius, 128)
                    : Config.DATA.generationRadius;

            List<ChunkPos> found = new ArrayList<>();
            state.getDistanceGraph().collectCompletedInRange(
                    player.chunkPosition(), radius, synced, found, CATCHUP_BATCH);
            if (found.isEmpty()) {
                tracker.clearBackfill(uuid);
                continue;
            }

            List<ChunkPos> batch = new ArrayList<>(found.size());
            for (ChunkPos pos : found) if (inSyncRange(player, pos)) batch.add(pos);
            if (batch.isEmpty()) continue;

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
                keepSynced(player, pos);
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
            keepSynced(player, pos);
        }
    }

    private static boolean inSyncRange(ServerPlayer player, ChunkPos pos) {
        double dx = player.getX() - pos.getMiddleBlockX();
        double dz = player.getZ() - pos.getMiddleBlockZ();
        return dx * dx + dz * dz <= NetworkHandler.syncRadiusSq();
    }

    private static void keepSynced(ServerPlayer player, ChunkPos pos) {
        if (!inSyncRange(player, pos)) return;
        LongSet synced = PlayerTracker.getInstance().getSyncedChunks(player.getUUID());
        if (synced != null) synced.add(pos.toLong());
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
