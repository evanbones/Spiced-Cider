package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.compat.voxy.VoxyIngest;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LodGen {
    private static final int TARGET_INTERVAL = 20;
    private static final int BUILT_MARGIN = 2;

    private static final Map<UUID, Integer> players = new ConcurrentHashMap<>();
    private static final Map<UUID, LongSet> sent = new HashMap<>();
    @Nullable
    private static volatile LodEngine engine;
    @Nullable
    private static volatile UUID host;
    private static List<LodTarget> targets = List.of();

    public static void start(MinecraftServer server) {
        if (engine != null) return;
        engine = new LodEngine(server, threads());
    }

    public static void stop() {
        LodEngine running = engine;
        engine = null;
        if (running != null) running.stop();
        players.clear();
        sent.clear();
        host = null;
        targets = List.of();
        LodEdge.clear();
    }

    public static void tick(MinecraftServer server) {
        LodEngine running = engine;
        if (running == null) return;
        if (server.getTickCount() % TARGET_INTERVAL == 0) {
            targets = buildTargets(server);
            running.update(targets);
        }
        running.delivery.tick(server, SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.lodGenerationSendRate, 100));
        LodEdge.tick(server, targets);
    }

    public static void hello(ServerPlayer player, int radius) {
        if (radius > 0) {
            players.put(player.getUUID(), radius);
        } else {
            players.remove(player.getUUID());
        }
    }

    public static void forget(ServerPlayer player) {
        players.remove(player.getUUID());
        sent.remove(player.getUUID());
        LodEngine running = engine;
        if (running != null) running.delivery.forget(player.server, player.getUUID());
        LodEdge.forget(player.getUUID());
    }

    public static void chunkSent(ServerPlayer player, ChunkPos pos) {
        if (players.containsKey(player.getUUID())) sent.computeIfAbsent(player.getUUID(), uuid -> new LongOpenHashSet()).add(pos.toLong());
    }

    public static void chunkUnwatched(ServerPlayer player, ServerLevel level, ChunkPos pos) {
        LongSet chunks = sent.get(player.getUUID());
        if (chunks != null && chunks.remove(pos.toLong())) LodLedger.get(level).chunks(player.getUUID()).add(pos.toLong());
    }

    public static void reset(ServerPlayer player) {
        for (ServerLevel level : player.server.getAllLevels()) {
            LodLedger.get(level).clear(player.getUUID());
        }
        LodEngine running = engine;
        if (running != null) running.reset(player.getUUID());
    }

    public static boolean tracked(ServerPlayer player) {
        return players.containsKey(player.getUUID());
    }

    public static int remaining(UUID player) {
        LodEngine running = engine;
        return running == null ? -1 : running.remaining(player);
    }

    public static int queued(UUID player) {
        LodEngine running = engine;
        return running == null ? 0 : running.delivery.queued(player);
    }

    public static double rate() {
        LodEngine running = engine;
        return running == null ? 0 : running.rate();
    }

    @Nullable
    public static String debugLine() {
        LodEngine running = engine;
        UUID owner = host;
        if (running == null || owner == null || !enabled()) return null;
        int left = running.remaining(owner);
        if (left < 0) return null;
        return String.format(Locale.ROOT, "LoD generation: %d chunks left, %.0f/s, %d done", left, running.rate(), running.completed());
    }

    static int threads() {
        int configured = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.lodGenerationThreads, 0);
        return configured > 0 ? configured : autoThreads();
    }

    public static int autoThreads() {
        return Math.clamp(Runtime.getRuntime().availableProcessors() / 8, 1, 4);
    }

    static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.lodGeneration, true);
    }

    private static List<LodTarget> buildTargets(MinecraftServer server) {
        if (!enabled()) return List.of();
        int maxRadius = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.lodGenerationMaxRadius, 128);
        List<LodTarget> next = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : players.entrySet()) {
            UUID uuid = entry.getKey();
            int radius = Math.min(entry.getValue(), maxRadius);
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) continue;
            boolean isHost = !server.isDedicatedServer() && server.isSingleplayerOwner(player.getGameProfile()) && VoxyIngest.available();
            if (isHost) host = uuid;
            ServerLevel level = player.serverLevel();
            ChunkPos pos = player.chunkPosition();
            int builtDistance = Mth.clamp(player.requestedViewDistance(), 2, server.getPlayerList().getViewDistance()) - BUILT_MARGIN;
            next.add(new LodTarget(uuid, level, pos.x, pos.z, radius, builtDistance, isHost, LodLedger.get(level).chunks(uuid)));
        }
        return next;
    }
}
