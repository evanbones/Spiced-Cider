package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.mixin.minecraft.ChunkAccessLodAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

final class LodDimension {
    private static final int STARTS_RADIUS = 8;
    private static final int FAILURE_REACH = 3;
    private static final int MAX_NODES = 12000;
    private static final int MAX_GENERATED = Runtime.getRuntime().maxMemory() < 6L << 30 ? 1250 : 2500;
    private static final int DISK_PER_PLAN = 16;

    final ServerLevel level;
    private final LodDisk disk;
    private final WorldGenContext context;
    private final Registry<Biome> biomes;
    private final Scheduler scheduler;
    private final Consumer<LodColumn> delivered;
    private final LongConsumer abandoned;
    private final ThreadLocal<LodLight> lights;
    private final ConcurrentHashMap<Long, LodNode> nodes = new ConcurrentHashMap<>();
    private final LodNode.Holder missing = new LodNode.Holder(ChunkPos.ZERO, null);
    private final List<Plan> plans = new ArrayList<>();
    private final Long2ObjectOpenHashMap<Plan> wantedBy = new Long2ObjectOpenHashMap<>();
    private final LongSet inFlight = new LongOpenHashSet();
    private final LongLinkedOpenHashSet candidates = new LongLinkedOpenHashSet();
    private final LongSet lighting = new LongOpenHashSet();
    private final LongArrayFIFOQueue diskQueue = new LongArrayFIFOQueue();
    private int diskRunning;
    private int lastX;
    private int lastZ;
    private int warnings;

    LodDimension(ServerLevel level, Scheduler scheduler, Consumer<LodColumn> delivered, LongConsumer abandoned) {
        this.level = level;
        this.scheduler = scheduler;
        this.delivered = delivered;
        this.abandoned = abandoned;
        this.biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        this.disk = new LodDisk(level, biomes);
        this.lights = ThreadLocal.withInitial(() -> new LodLight(level));
        this.context = new WorldGenContext(level, level.getChunkSource().getGenerator(), level.getServer().getStructureManager(),
                level.getChunkSource().getLightEngine(), null);
    }

    boolean pending(long pos) {
        return inFlight.contains(pos);
    }

    int backlog() {
        return plans.size() + (diskQueue.size() + diskRunning) / DISK_PER_PLAN;
    }

    void request(LongSet wanted, int centerX, int centerZ) {
        lastX = centerX;
        lastZ = centerZ;
        LongSet generate = new LongOpenHashSet();
        for (long pos : wanted) {
            inFlight.add(pos);
            if (disk.exists(ChunkPos.getX(pos), ChunkPos.getZ(pos))) {
                diskQueue.enqueue(pos);
            } else {
                generate.add(pos);
            }
        }
        if (!generate.isEmpty()) plan(generate);
    }

    void dispatch() {
        while (scheduler.hasCapacity() && !diskQueue.isEmpty()) readFromDisk(diskQueue.dequeueLong());
        LongIterator iterator = candidates.iterator();
        while (scheduler.hasCapacity() && iterator.hasNext()) {
            long pos = iterator.nextLong();
            iterator.remove();
            if (lightReady(pos)) {
                startLight(pos);
                continue;
            }
            LodNode node = nodes.get(pos);
            if (node == null) continue;
            ChunkStatus next = nextStep(node);
            if (next != null) startStep(node, next);
        }
    }

    void clear() {
        nodes.clear();
        plans.clear();
        wantedBy.clear();
        inFlight.clear();
        candidates.clear();
        lighting.clear();
        diskQueue.clear();
        disk.clear();
    }

    private void plan(LongSet wanted) {
        Plan plan = new Plan(wanted);
        for (long pos : plan.starts) {
            LodNode node = nodes.computeIfAbsent(pos, key -> new LodNode(ChunkPos.getX(key), ChunkPos.getZ(key),
                    new ProtoChunk(new ChunkPos(key), UpgradeData.EMPTY, level, biomes, null)));
            node.refs++;
            if (!node.failed && (node.status().isBefore(ChunkStatus.STRUCTURE_STARTS) || node.runningStarts)) plan.startsPending.add(pos);
            candidates.add(pos);
        }
        for (long pos : plan.biomes) nodes.get(pos).needBiomes++;
        for (long pos : plan.terrain) nodes.get(pos).needCarvers++;
        for (long pos : plan.features) nodes.get(pos).needFeatures++;
        plans.add(plan);
        for (long pos : wanted) wantedBy.put(pos, plan);
        for (long pos : new LongOpenHashSet(wanted)) {
            if (failedNear(ChunkPos.getX(pos), ChunkPos.getZ(pos))) giveUp(plan, pos);
        }
        if (plan.startsPending.isEmpty() && plans.contains(plan)) unlock(plan);
    }

    private void unlock(Plan plan) {
        plan.unlocked = true;
        for (long pos : plan.biomes) {
            nodes.get(pos).unlocked++;
            candidates.add(pos);
        }
    }

    private void finish(Plan plan) {
        plans.remove(plan);
        for (long pos : plan.starts) nodes.get(pos).refs--;
        for (long pos : plan.biomes) {
            LodNode node = nodes.get(pos);
            node.needBiomes--;
            if (plan.unlocked) node.unlocked--;
        }
        for (long pos : plan.terrain) nodes.get(pos).needCarvers--;
        for (long pos : plan.features) nodes.get(pos).needFeatures--;
        evict();
    }

    private ChunkStatus nextStep(LodNode node) {
        if (node.running || node.failed) return null;
        ChunkStatus status = node.status();
        if (!status.isBefore(node.required())) return null;
        ChunkStatus next = ChunkStatus.getStatusList().get(status.getIndex() + 1);
        if (next == ChunkStatus.STRUCTURE_STARTS) return next;
        if (node.unlocked == 0) return null;
        if (next == ChunkStatus.NOISE || next == ChunkStatus.SURFACE) return neighbours(node.x, node.z, ChunkStatus.BIOMES) ? next : null;
        if (next == ChunkStatus.FEATURES) {
            return neighbours(node.x, node.z, ChunkStatus.CARVERS) && !busyNear(node.x, node.z, true) ? next : null;
        }
        return next;
    }

    private boolean lightReady(long pos) {
        if (!wantedBy.containsKey(pos) || lighting.contains(pos)) return false;
        int x = ChunkPos.getX(pos);
        int z = ChunkPos.getZ(pos);
        return neighbours(x, z, ChunkStatus.FEATURES) && !busyNear(x, z, false);
    }

    private void startStep(LodNode node, ChunkStatus status) {
        node.running = true;
        node.runningFeatures = status == ChunkStatus.FEATURES;
        node.runningStarts = status == ChunkStatus.STRUCTURE_STARTS;
        scheduler.submit(() -> {
            try {
                apply(node, status);
                return true;
            } catch (Throwable throwable) {
                warn(node.x, node.z, throwable);
                return false;
            }
        }, ok -> stepDone(node, status, Boolean.TRUE.equals(ok)));
    }

    private void stepDone(LodNode node, ChunkStatus status, boolean ok) {
        node.running = false;
        node.runningFeatures = false;
        node.runningStarts = false;
        if (!ok) {
            fail(node, status);
        } else if (status == ChunkStatus.STRUCTURE_STARTS) {
            startsDone(ChunkPos.asLong(node.x, node.z));
        }
        touch(node.x, node.z);
    }

    private void startsDone(long pos) {
        for (Plan plan : new ArrayList<>(plans)) {
            if (!plan.unlocked && plan.startsPending.remove(pos) && plan.startsPending.isEmpty()) unlock(plan);
        }
    }

    private void fail(LodNode node, ChunkStatus status) {
        node.failed = true;
        if (status == ChunkStatus.STRUCTURE_STARTS) startsDone(ChunkPos.asLong(node.x, node.z));
        for (Plan plan : new ArrayList<>(plans)) {
            for (long pos : new LongOpenHashSet(plan.wanted)) {
                if (Math.max(Math.abs(ChunkPos.getX(pos) - node.x), Math.abs(ChunkPos.getZ(pos) - node.z)) <= FAILURE_REACH) {
                    giveUp(plan, pos);
                }
            }
        }
    }

    private void giveUp(Plan plan, long pos) {
        if (lighting.contains(pos)) return;
        settle(plan, pos);
        abandoned.accept(pos);
    }

    private void settle(Plan plan, long pos) {
        plan.wanted.remove(pos);
        wantedBy.remove(pos);
        inFlight.remove(pos);
        if (plan.wanted.isEmpty() && plans.contains(plan)) finish(plan);
    }

    private void startLight(long pos) {
        int x = ChunkPos.getX(pos);
        int z = ChunkPos.getZ(pos);
        ChunkAccess[] region = new ChunkAccess[9];
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                region[(dz + 1) * 3 + dx + 1] = nodes.get(ChunkPos.asLong(x + dx, z + dz)).chunk;
            }
        }
        lighting.add(pos);
        scheduler.submit(() -> {
            try {
                long start = System.nanoTime();
                LodLight light = lights.get();
                light.solve(region, 3, 3);
                LodColumn column = new LodColumn(x, z, light.sections(region[4], 1, 1));
                LodStats.add(LodStats.LIGHT, System.nanoTime() - start, 1);
                return column;
            } catch (Throwable throwable) {
                warn(x, z, throwable);
                return null;
            }
        }, column -> lightDone(pos, column));
    }

    private void lightDone(long pos, LodColumn column) {
        lighting.remove(pos);
        Plan plan = wantedBy.get(pos);
        if (plan != null) settle(plan, pos);
        if (column != null) {
            delivered.accept(column);
        } else {
            abandoned.accept(pos);
        }
        touch(ChunkPos.getX(pos), ChunkPos.getZ(pos));
    }

    private void readFromDisk(long pos) {
        diskRunning++;
        int x = ChunkPos.getX(pos);
        int z = ChunkPos.getZ(pos);
        scheduler.submit(() -> {
            try {
                long start = System.nanoTime();
                LodColumn column = disk.read(x, z, lights.get());
                LodStats.add(LodStats.DISK, System.nanoTime() - start, 1);
                return column;
            } catch (Throwable throwable) {
                SpicedCider.LOGGER.warn("Could not read chunk {}, {} for LoD, generating it instead", x, z, throwable);
                return null;
            }
        }, column -> {
            diskRunning--;
            if (column != null) {
                inFlight.remove(pos);
                delivered.accept(column);
            } else {
                LongSet single = new LongOpenHashSet();
                single.add(pos);
                plan(single);
            }
        });
    }

    private void apply(LodNode node, ChunkStatus status) {
        ChunkStep step = ChunkPyramid.GENERATION_PYRAMID.getStepTo(status);
        int radius = Math.max(0, step.directDependencies().size() - 1);
        StaticCache2D<GenerationChunkHolder> cache = StaticCache2D.create(node.x, node.z, radius, this::holder);
        long start = System.nanoTime();
        ChunkAccess result = step.apply(context, cache, node.chunk).join();
        LodStats.add(status.getIndex(), System.nanoTime() - start, 1);
        if (result instanceof ProtoChunk proto && proto != node.chunk) node.chunk = proto;
        if (status == ChunkStatus.CARVERS) ((ChunkAccessLodAccessor) node.chunk).spicedcider$setNoiseChunk(null);
    }

    private GenerationChunkHolder holder(int x, int z) {
        LodNode node = nodes.get(ChunkPos.asLong(x, z));
        return node != null ? node.holder : missing;
    }

    private boolean neighbours(int x, int z, ChunkStatus status) {
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                LodNode node = nodes.get(ChunkPos.asLong(x + dx, z + dz));
                if (node == null || !node.reached(status)) return false;
            }
        }
        return true;
    }

    private boolean busyNear(int x, int z, boolean countLighting) {
        for (int dz = -2; dz <= 2; dz++) {
            for (int dx = -2; dx <= 2; dx++) {
                long pos = ChunkPos.asLong(x + dx, z + dz);
                LodNode node = nodes.get(pos);
                if ((node != null && node.runningFeatures) || (countLighting && lighting.contains(pos))) return true;
            }
        }
        return false;
    }

    private boolean failedNear(int x, int z) {
        for (int dz = -FAILURE_REACH; dz <= FAILURE_REACH; dz++) {
            for (int dx = -FAILURE_REACH; dx <= FAILURE_REACH; dx++) {
                LodNode node = nodes.get(ChunkPos.asLong(x + dx, z + dz));
                if (node != null && node.failed) return true;
            }
        }
        return false;
    }

    private void touch(int x, int z) {
        for (int dz = -2; dz <= 2; dz++) {
            for (int dx = -2; dx <= 2; dx++) {
                long pos = ChunkPos.asLong(x + dx, z + dz);
                if (nodes.containsKey(pos) || wantedBy.containsKey(pos)) candidates.add(pos);
            }
        }
    }

    private void evict() {
        nodes.values().removeIf(node -> node.failed && node.refs == 0 && !node.running);
        int generated = 0;
        for (LodNode node : nodes.values()) {
            if (node.status().isOrAfter(ChunkStatus.NOISE)) generated++;
        }
        if (nodes.size() <= MAX_NODES && generated <= MAX_GENERATED) return;
        List<LodNode> idle = new ArrayList<>();
        for (LodNode node : nodes.values()) {
            if (node.refs == 0 && !node.running) idle.add(node);
        }
        idle.sort(Comparator.comparingLong(node -> -distanceSq(node, lastX, lastZ)));
        int keepNodes = MAX_NODES * 3 / 4;
        int keepGenerated = MAX_GENERATED * 3 / 4;
        for (LodNode node : idle) {
            if (nodes.size() <= keepNodes && generated <= keepGenerated) break;
            boolean heavy = node.status().isOrAfter(ChunkStatus.NOISE);
            if (nodes.size() <= keepNodes && !heavy) continue;
            nodes.remove(ChunkPos.asLong(node.x, node.z));
            if (heavy) generated--;
        }
    }

    private synchronized void warn(int x, int z, Throwable throwable) {
        if (warnings++ < 20) {
            SpicedCider.LOGGER.warn("LoD generation failed for chunk {}, {} in {}", x, z, level.dimension().location(), throwable);
        }
    }

    private static long distanceSq(LodNode node, int x, int z) {
        long dx = node.x - x;
        long dz = node.z - z;
        return dx * dx + dz * dz;
    }

    private static LongSet dilate(LongSet positions, int radius) {
        LongSet out = new LongOpenHashSet();
        for (long pos : positions) {
            int x = ChunkPos.getX(pos);
            int z = ChunkPos.getZ(pos);
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    out.add(ChunkPos.asLong(x + dx, z + dz));
                }
            }
        }
        return out;
    }

    interface Scheduler {
        boolean hasCapacity();

        <T> void submit(Supplier<T> work, Consumer<T> done);
    }

    private static final class Plan {
        final LongSet wanted;
        final LongSet features;
        final LongSet terrain;
        final LongSet biomes;
        final LongSet starts;
        final LongSet startsPending = new LongOpenHashSet();
        boolean unlocked;

        Plan(LongSet wanted) {
            this.wanted = new LongOpenHashSet(wanted);
            this.features = dilate(wanted, 1);
            this.terrain = dilate(features, 1);
            this.biomes = dilate(terrain, 1);
            this.starts = dilate(biomes, STARTS_RADIUS);
        }
    }
}
