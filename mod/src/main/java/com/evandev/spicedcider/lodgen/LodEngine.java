package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.SpicedCider;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class LodEngine implements LodDimension.Scheduler {
    private static final int TILE = 8;
    private static final long NONE = Long.MIN_VALUE;
    private static final long MAX_TICK_NANOS = 45_000_000L;
    private static final long STATS_INTERVAL_MS = 5_000L;
    private static final long CURSOR_LIFETIME_MS = 30_000L;

    final LodDelivery delivery = new LodDelivery();
    private final MinecraftServer server;
    private final int threads;
    private final int maxBacklog;
    private final ExecutorService pool;
    private final Thread coordinator;
    private final BlockingQueue<Runnable> events = new LinkedBlockingQueue<>();
    private final Map<ResourceKey<Level>, LodDimension> dimensions = new HashMap<>();
    private final Map<ResourceKey<Level>, LongSet> abandoned = new HashMap<>();
    private final Map<UUID, Cursor> cursors = new HashMap<>();
    private final Queue<UUID> resets = new ConcurrentLinkedQueue<>();
    private final Map<UUID, Integer> remaining = new ConcurrentHashMap<>();
    private final AtomicLong completed = new AtomicLong();
    private volatile List<LodTarget> targets = List.of();
    private volatile boolean running = true;
    private volatile double rate;
    private int busy;
    private long statsTime;
    private long statsCompleted;
    private int rotation;

    LodEngine(MinecraftServer server, int threads) {
        this.server = server;
        this.threads = threads;
        this.maxBacklog = threads / 2 + 2;
        this.pool = Executors.newFixedThreadPool(threads, LodThreads.factory("Spiced Cider LoD worker"));
        this.coordinator = LodThreads.create(this::loop, "Spiced Cider LoD coordinator");
        this.coordinator.start();
    }

    void update(List<LodTarget> next) {
        targets = List.copyOf(next);
    }

    void reset(UUID player) {
        resets.add(player);
    }

    int remaining(UUID player) {
        return remaining.getOrDefault(player, -1);
    }

    double rate() {
        return rate;
    }

    long completed() {
        return completed.get();
    }

    void stop() {
        running = false;
        coordinator.interrupt();
        pool.shutdownNow();
        try {
            coordinator.join(10_000L);
            pool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        if (!coordinator.isAlive()) {
            dimensions.values().forEach(LodDimension::clear);
            dimensions.clear();
        }
        events.clear();
        delivery.clear(server);
        targets = List.of();
    }

    @Override
    public boolean hasCapacity() {
        return busy < threads;
    }

    @Override
    public <T> void submit(Supplier<T> work, Consumer<T> done) {
        busy++;
        try {
            pool.execute(() -> {
                T result = null;
                try {
                    result = work.get();
                } finally {
                    T finished = result;
                    events.add(() -> {
                        busy--;
                        done.accept(finished);
                    });
                }
            });
        } catch (RejectedExecutionException exception) {
            busy--;
        }
    }

    private void loop() {
        while (running) {
            try {
                UUID reset;
                while ((reset = resets.poll()) != null) cursors.remove(reset);
                Runnable event;
                while ((event = events.poll()) != null) event.run();

                List<LodTarget> snapshot = targets;
                dropIdleDimensions(snapshot);
                updateStats(snapshot);
                boolean planning = !snapshot.isEmpty() && LodGen.enabled() && server.getAverageTickTimeNanos() <= MAX_TICK_NANOS;
                if (planning) planTiles(snapshot);
                for (LodDimension dimension : dimensions.values()) dimension.dispatch();

                event = events.poll(busy > 0 ? 100L : 250L, TimeUnit.MILLISECONDS);
                if (event != null) event.run();
            } catch (InterruptedException exception) {
                break;
            } catch (Throwable throwable) {
                SpicedCider.LOGGER.error("LoD generation loop failed", throwable);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException exception) {
                    break;
                }
            }
        }
    }

    private void planTiles(List<LodTarget> snapshot) {
        int attempts = snapshot.size();
        while (backlog() < maxBacklog && attempts > 0) {
            LodTarget target = snapshot.get(Math.floorMod(rotation++, snapshot.size()));
            if (!delivery.accepting(target)) {
                attempts--;
                continue;
            }
            long seed = nextSeed(target);
            if (seed == NONE) {
                attempts--;
                continue;
            }
            planTile(target.level(), Math.floorDiv(ChunkPos.getX(seed), TILE), Math.floorDiv(ChunkPos.getZ(seed), TILE), snapshot);
        }
    }

    private int backlog() {
        int total = 0;
        for (LodDimension dimension : dimensions.values()) total += dimension.backlog();
        return total;
    }

    private void planTile(ServerLevel level, int tileX, int tileZ, List<LodTarget> snapshot) {
        LodDimension dimension = dimension(level);
        LongSet skip = abandoned.computeIfAbsent(level.dimension(), key -> new LongOpenHashSet());
        LongSet wanted = new LongOpenHashSet();
        for (int z = tileZ * TILE; z < tileZ * TILE + TILE; z++) {
            for (int x = tileX * TILE; x < tileX * TILE + TILE; x++) {
                long pos = ChunkPos.asLong(x, z);
                if (skip.contains(pos) || dimension.pending(pos)) continue;
                for (LodTarget target : snapshot) {
                    if (target.level() == level && target.wants(x, z)) {
                        wanted.add(pos);
                        break;
                    }
                }
            }
        }
        if (!wanted.isEmpty()) dimension.request(wanted, tileX * TILE + TILE / 2, tileZ * TILE + TILE / 2);
    }

    private LodDimension dimension(ServerLevel level) {
        return dimensions.computeIfAbsent(level.dimension(), key -> new LodDimension(level, this,
                column -> deliver(level, column),
                pos -> abandoned.computeIfAbsent(key, k -> new LongOpenHashSet()).add(pos)));
    }

    private void deliver(ServerLevel level, LodColumn column) {
        List<LodTarget> recipients = new ArrayList<>();
        for (LodTarget target : targets) {
            if (target.level() == level && target.wants(column.x(), column.z())) recipients.add(target);
        }
        if (recipients.isEmpty()) return;
        delivery.deliver(level, column, recipients);
        completed.incrementAndGet();
    }

    private long nextSeed(LodTarget target) {
        Cursor cursor = cursors.get(target.player());
        if (cursor == null || cursor.level != target.level().dimension() || cursor.x != target.chunkX() || cursor.z != target.chunkZ()
                || cursor.radius != target.radius() || System.currentTimeMillis() - cursor.created > CURSOR_LIFETIME_MS) {
            cursor = new Cursor(target.level().dimension(), target.chunkX(), target.chunkZ(), target.radius());
            cursors.put(target.player(), cursor);
        }
        LongSet skip = abandoned.getOrDefault(target.level().dimension(), LongSet.of());
        LodDimension dimension = dimensions.get(target.level().dimension());
        for (; cursor.ring <= target.radius(); cursor.ring++) {
            int ring = cursor.ring;
            for (int i = -ring; i <= ring; i++) {
                long found = check(target, skip, dimension, target.chunkX() + i, target.chunkZ() - ring);
                if (found == NONE) found = check(target, skip, dimension, target.chunkX() + i, target.chunkZ() + ring);
                if (found == NONE && i != -ring && i != ring) {
                    found = check(target, skip, dimension, target.chunkX() - ring, target.chunkZ() + i);
                    if (found == NONE) found = check(target, skip, dimension, target.chunkX() + ring, target.chunkZ() + i);
                }
                if (found != NONE) return found;
            }
        }
        return NONE;
    }

    private static long check(LodTarget target, LongSet skip, LodDimension dimension, int x, int z) {
        if (!target.wants(x, z)) return NONE;
        long pos = ChunkPos.asLong(x, z);
        return skip.contains(pos) || (dimension != null && dimension.pending(pos)) ? NONE : pos;
    }

    private void updateStats(List<LodTarget> snapshot) {
        long now = System.currentTimeMillis();
        if (now - statsTime < STATS_INTERVAL_MS) return;
        long done = completed.get();
        if (statsTime != 0) rate = (done - statsCompleted) * 1000.0 / (now - statsTime);
        statsTime = now;
        statsCompleted = done;
        remaining.clear();
        for (LodTarget target : snapshot) {
            LongSet skip = abandoned.getOrDefault(target.level().dimension(), LongSet.of());
            int missing = 0;
            int radius = target.radius();
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    int x = target.chunkX() + dx;
                    int z = target.chunkZ() + dz;
                    if (target.wants(x, z) && !skip.contains(ChunkPos.asLong(x, z))) missing++;
                }
            }
            remaining.put(target.player(), missing);
        }
    }

    private void dropIdleDimensions(List<LodTarget> snapshot) {
        dimensions.entrySet().removeIf(entry -> {
            for (LodTarget target : snapshot) {
                if (target.level().dimension() == entry.getKey()) return false;
            }
            if (entry.getValue().backlog() > 0) return false;
            entry.getValue().clear();
            return true;
        });
    }

    private static final class Cursor {
        final ResourceKey<Level> level;
        final int x;
        final int z;
        final int radius;
        final long created = System.currentTimeMillis();
        int ring = 1;

        Cursor(ResourceKey<Level> level, int x, int z, int radius) {
            this.level = level;
            this.x = x;
            this.z = z;
            this.radius = radius;
        }
    }
}
