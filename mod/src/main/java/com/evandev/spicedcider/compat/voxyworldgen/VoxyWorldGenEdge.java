package com.evandev.spicedcider.compat.voxyworldgen;

import com.evandev.spicedcider.mixin.voxyworldgen.ChunkGenerationManagerAccessor;
import com.evandev.spicedcider.mixin.voxyworldgen.DimensionStateAccessor;
import com.evandev.spicedcider.networking.GenerationEdgePayload;
import com.ethan.voxyworldgenv2.core.ChunkGenerationManager;
import com.ethan.voxyworldgenv2.core.Config;
import com.ethan.voxyworldgenv2.core.PlayerTracker;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class VoxyWorldGenEdge {
    public static final float NONE = -1.0F;
    private static final float RESEND_DELTA = 4.0F;

    private static final ExecutorService SCANNER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "Spiced Cider generation edge");
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    });
    private static final AtomicBoolean scanning = new AtomicBoolean();
    private static final Map<UUID, Float> sent = new ConcurrentHashMap<>();

    public static void tick(MinecraftServer server) {
        if (!scanning.compareAndSet(false, true)) return;
        Map<?, ?> states = ((ChunkGenerationManagerAccessor) (Object) ChunkGenerationManager.getInstance()).getDimensionStates();
        int radius = Config.DATA.generationRadius;
        boolean enabled = Config.DATA.enabled;
        int viewDistance = server.getPlayerList().getViewDistance();

        List<Job> jobs = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!PlayerTracker.getInstance().isModded(player.getUUID())) continue;
            LongSet completed = enabled && states.get(player.level().dimension()) instanceof DimensionStateAccessor state
                    ? state.getCompletedChunks() : null;
            jobs.add(new Job(player, completed, player.getX(), player.getZ(), radius, viewDistance));
        }
        if (jobs.isEmpty()) {
            scanning.set(false);
            return;
        }

        SCANNER.execute(() -> {
            try {
                for (Job job : jobs) {
                    float distance = job.completed == null ? NONE : scan(job);
                    server.execute(() -> send(job.player, distance));
                }
            } finally {
                scanning.set(false);
            }
        });
    }

    public static void forget(UUID uuid) {
        sent.remove(uuid);
    }

    private static void send(ServerPlayer player, float distance) {
        if (player.hasDisconnected()) return;
        Float previous = sent.get(player.getUUID());
        if (previous != null && (previous == distance || (previous >= 0 && distance >= 0 && Math.abs(previous - distance) < RESEND_DELTA))) return;
        sent.put(player.getUUID(), distance);
        PacketDistributor.sendToPlayer(player, new GenerationEdgePayload(distance));
    }

    private static float scan(Job job) {
        int centerX = SectionPos.blockToSectionCoord(job.x);
        int centerZ = SectionPos.blockToSectionCoord(job.z);
        double best = Double.MAX_VALUE;
        synchronized (job.completed) {
            for (int ring = job.viewDistance + 1; ring <= job.radius + 1; ring++) {
                if ((ring - 1) * 16.0 >= best) break;
                for (int i = -ring; i <= ring; i++) {
                    best = check(job, centerX + i, centerZ - ring, best);
                    best = check(job, centerX + i, centerZ + ring, best);
                    if (i == -ring || i == ring) continue;
                    best = check(job, centerX - ring, centerZ + i, best);
                    best = check(job, centerX + ring, centerZ + i, best);
                }
            }
        }
        return (float) Math.min(best, (job.radius + 1) * 16.0);
    }

    private static double check(Job job, int chunkX, int chunkZ, double best) {
        if (job.completed.contains(ChunkPos.asLong(chunkX, chunkZ))) return best;
        double dx = Math.max(0.0, Math.max(chunkX * 16.0 - job.x, job.x - (chunkX * 16.0 + 16.0)));
        double dz = Math.max(0.0, Math.max(chunkZ * 16.0 - job.z, job.z - (chunkZ * 16.0 + 16.0)));
        return Math.min(best, Math.sqrt(dx * dx + dz * dz));
    }

    private record Job(ServerPlayer player, LongSet completed, double x, double z, int radius, int viewDistance) {
    }
}
