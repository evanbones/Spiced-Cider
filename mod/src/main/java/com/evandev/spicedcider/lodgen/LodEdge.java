package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.networking.GenerationEdgePayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
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
    private static final double LOBE = 0.25;
    private static final int EAST = 0;
    private static final int WEST = 1;
    private static final int SOUTH = 2;
    private static final int NORTH = 3;

    private static final ExecutorService SCANNER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "Spiced Cider generation edge");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });
    private static final AtomicBoolean scanning = new AtomicBoolean();
    private static final Map<UUID, float[]> sent = new ConcurrentHashMap<>();

    static void tick(MinecraftServer server, List<LodTarget> targets) {
        if (server.getTickCount() % INTERVAL != 0 || targets.isEmpty() || !scanning.compareAndSet(false, true)) return;
        SCANNER.execute(() -> {
            try {
                for (LodTarget target : targets) {
                    float[] distances = scan(target);
                    server.execute(() -> send(server.getPlayerList().getPlayer(target.player()), distances));
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

    private static void send(ServerPlayer player, float[] distances) {
        if (player == null || player.hasDisconnected()) return;
        float[] previous = sent.get(player.getUUID());
        if (previous != null && unchanged(previous, distances)) return;
        sent.put(player.getUUID(), distances);
        PacketDistributor.sendToPlayer(player, new GenerationEdgePayload(distances));
    }

    private static boolean unchanged(float[] previous, float[] distances) {
        for (int i = 0; i < distances.length; i++) {
            if (previous[i] == distances[i]) continue;
            if (previous[i] < 0 || distances[i] < 0 || Math.abs(previous[i] - distances[i]) >= RESEND_DELTA) return false;
        }
        return true;
    }

    private static float[] scan(LodTarget target) {
        double originX = target.chunkX() * 16.0 + 8.0;
        double originZ = target.chunkZ() * 16.0 + 8.0;
        double limit = (target.radius() + 1) * 16.0;
        double[] best = new double[4];
        Arrays.fill(best, limit);
        for (int ring = 1; ring <= target.radius() + 1; ring++) {
            double reach = (ring - 1) * 16.0;
            if (reach >= best[EAST] && reach >= best[WEST] && reach >= best[SOUTH] && reach >= best[NORTH]) break;
            for (int i = -ring; i <= ring; i++) {
                check(target, target.chunkX() + i, target.chunkZ() - ring, originX, originZ, best);
                check(target, target.chunkX() + i, target.chunkZ() + ring, originX, originZ, best);
                if (i == -ring || i == ring) continue;
                check(target, target.chunkX() - ring, target.chunkZ() + i, originX, originZ, best);
                check(target, target.chunkX() + ring, target.chunkZ() + i, originX, originZ, best);
            }
        }
        return new float[]{(float) best[EAST], (float) best[WEST], (float) best[SOUTH], (float) best[NORTH]};
    }

    private static void check(LodTarget target, int chunkX, int chunkZ, double originX, double originZ, double[] best) {
        if (!target.wants(chunkX, chunkZ)) return;
        double dx = Math.max(0.0, Math.max(chunkX * 16.0 - originX, originX - (chunkX * 16.0 + 16.0)));
        double dz = Math.max(0.0, Math.max(chunkZ * 16.0 - originZ, originZ - (chunkZ * 16.0 + 16.0)));
        double distance = Math.sqrt(dx * dx + dz * dz);
        double centerX = chunkX * 16.0 + 8.0 - originX;
        double centerZ = chunkZ * 16.0 + 8.0 - originZ;
        double lengthSquared = centerX * centerX + centerZ * centerZ;
        double weightX = centerX * centerX / lengthSquared;
        double weightZ = centerZ * centerZ / lengthSquared;
        double covered = (weightX >= LOBE ? weightX : 0.0) + (weightZ >= LOBE ? weightZ : 0.0);
        double limited = distance * covered;
        if (weightX >= LOBE) {
            int axis = centerX > 0.0 ? EAST : WEST;
            best[axis] = Math.min(best[axis], limited);
        }
        if (weightZ >= LOBE) {
            int axis = centerZ > 0.0 ? SOUTH : NORTH;
            best[axis] = Math.min(best[axis], limited);
        }
    }
}
