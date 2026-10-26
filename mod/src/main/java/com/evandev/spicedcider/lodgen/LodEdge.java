package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.networking.GenerationEdgePayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

final class LodEdge {
    static final float NONE = -1.0F;
    private static final int INTERVAL = 10;
    private static final float RESEND_DELTA = 4.0F;

    private static final ExecutorService SCANNER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "Spiced Cider generation edge");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });
    private static final AtomicBoolean scanning = new AtomicBoolean();
    private static final Map<UUID, Float> sent = new ConcurrentHashMap<>();

    static void tick(MinecraftServer server, List<LodTarget> targets) {
        if (server.getTickCount() % INTERVAL != 0 || targets.isEmpty() || !scanning.compareAndSet(false, true)) return;
        SCANNER.execute(() -> {
            try {
                for (LodTarget target : targets) {
                    float distance = scan(target);
                    server.execute(() -> send(server.getPlayerList().getPlayer(target.player()), distance));
                }
            } finally {
                scanning.set(false);
            }
        });
    }

    static void forget(UUID player) {
        sent.remove(player);
    }

    static void clear() {
        sent.clear();
    }

    private static void send(ServerPlayer player, float distance) {
        if (player == null || player.hasDisconnected()) return;
        Float previous = sent.get(player.getUUID());
        if (previous != null && (previous == distance || (previous >= 0 && distance >= 0 && Math.abs(previous - distance) < RESEND_DELTA))) return;
        sent.put(player.getUUID(), distance);
        PacketDistributor.sendToPlayer(player, new GenerationEdgePayload(distance));
    }

    private static float scan(LodTarget target) {
        double originX = target.chunkX() * 16.0 + 8.0;
        double originZ = target.chunkZ() * 16.0 + 8.0;
        double best = Double.MAX_VALUE;
        for (int ring = 1; ring <= target.radius() + 1; ring++) {
            if ((ring - 1) * 16.0 >= best) break;
            for (int i = -ring; i <= ring; i++) {
                best = check(target, target.chunkX() + i, target.chunkZ() - ring, originX, originZ, best);
                best = check(target, target.chunkX() + i, target.chunkZ() + ring, originX, originZ, best);
                if (i == -ring || i == ring) continue;
                best = check(target, target.chunkX() - ring, target.chunkZ() + i, originX, originZ, best);
                best = check(target, target.chunkX() + ring, target.chunkZ() + i, originX, originZ, best);
            }
        }
        return (float) Math.min(best, (target.radius() + 1) * 16.0);
    }

    private static double check(LodTarget target, int chunkX, int chunkZ, double originX, double originZ, double best) {
        if (!target.wants(chunkX, chunkZ)) return best;
        double dx = Math.max(0.0, Math.max(chunkX * 16.0 - originX, originX - (chunkX * 16.0 + 16.0)));
        double dz = Math.max(0.0, Math.max(chunkZ * 16.0 - originZ, originZ - (chunkZ * 16.0 + 16.0)));
        return Math.min(best, Math.sqrt(dx * dx + dz * dz));
    }
}
