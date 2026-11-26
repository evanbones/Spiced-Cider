package com.evandev.spicedcider.compat.beams;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

public final class BeamCollisionIndex {
    private static final double HALF_WIDTH = 0.125;
    private static final double END_TRIM = 0.01;
    private static final double SEGMENT = 0.25;
    private static final double AXIS_EPSILON = 1.0E-4;
    private static final double INTERIOR_EPSILON = 1.0E-4;
    private static final double STEP_CLEARANCE = 0.6;
    private static final double SIDE_OFFSET = HALF_WIDTH * 0.9;
    private static final double SAMPLE_STEP = 1.0 / 32.0;
    private static volatile Memo lastClientCheck;
    private static final Map<Level, BeamCollisionIndex> INDICES = Collections.synchronizedMap(new WeakHashMap<>());

    private final Map<Long, List<Beam>> buckets = new HashMap<>();
    private long generation = Long.MIN_VALUE;

    private record Beam(Vec3 start, Vec3 end, AABB bounds, AABB[] boxes, VoxelShape[] shapes) {
    }

    public static boolean enabled() {
        return BeamsCompat.isLoaded() && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.solidBeams, false);
    }

    private static synchronized BeamCollisionIndex of(Level level) {
        BeamCollisionIndex index = INDICES.computeIfAbsent(level, k -> new BeamCollisionIndex());
        long generation = BeamSources.generation();
        if (index.generation != generation) {
            index.rebuild(level);
            index.generation = generation;
        }
        return index;
    }

    private void rebuild(Level level) {
        buckets.clear();
        for (Vec3[] ends : BeamSources.beams(level)) {
            Beam beam = bake(ends[0], ends[1]);
            if (beam == null) continue;
            for (long key : bucketKeys(beam.bounds())) {
                buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(beam);
            }
        }
    }

    @Nullable
    private static Beam bake(Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length < END_TRIM * 4) return null;
        Vec3 axis = delta.scale(1.0 / length);
        Vec3 from = start.add(axis.scale(END_TRIM));
        Vec3 to = end.subtract(axis.scale(END_TRIM));
        List<AABB> boxes = new ArrayList<>();
        int aligned = (Math.abs(axis.x) < AXIS_EPSILON ? 1 : 0) + (Math.abs(axis.y) < AXIS_EPSILON ? 1 : 0) + (Math.abs(axis.z) < AXIS_EPSILON ? 1 : 0);
        if (aligned == 2) {
            boxes.add(prism(from, to, axis));
        } else {
            int segments = Math.max(1, Mth.ceil((length - END_TRIM * 2) / SEGMENT));
            for (int i = 0; i < segments; i++) {
                Vec3 a = from.lerp(to, (double) i / segments);
                Vec3 b = from.lerp(to, (double) (i + 1) / segments);
                boxes.add(new AABB(a, b).inflate(HALF_WIDTH * 0.75));
            }
        }
        AABB[] boxArray = boxes.toArray(new AABB[0]);
        VoxelShape[] shapes = new VoxelShape[boxArray.length];
        AABB bounds = boxArray[0];
        for (int i = 0; i < boxArray.length; i++) {
            shapes[i] = Shapes.create(boxArray[i]);
            bounds = bounds.minmax(boxArray[i]);
        }
        return new Beam(start, end, bounds, boxArray, shapes);
    }

    private static AABB prism(Vec3 from, Vec3 to, Vec3 axis) {
        double ix = Math.abs(axis.x) < AXIS_EPSILON ? HALF_WIDTH : 0;
        double iy = Math.abs(axis.y) < AXIS_EPSILON ? HALF_WIDTH : 0;
        double iz = Math.abs(axis.z) < AXIS_EPSILON ? HALF_WIDTH : 0;
        return new AABB(from, to).inflate(ix, iy, iz);
    }

    private static long[] bucketKeys(AABB bounds) {
        int minX = SectionPos.blockToSectionCoord(Mth.floor(bounds.minX));
        int maxX = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxX));
        int minZ = SectionPos.blockToSectionCoord(Mth.floor(bounds.minZ));
        int maxZ = SectionPos.blockToSectionCoord(Mth.floor(bounds.maxZ));
        long[] keys = new long[(maxX - minX + 1) * (maxZ - minZ + 1)];
        int i = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                keys[i++] = ChunkPos.asLong(x, z);
            }
        }
        return keys;
    }

    private List<Beam> near(AABB area) {
        int minX = SectionPos.blockToSectionCoord(Mth.floor(area.minX));
        int maxX = SectionPos.blockToSectionCoord(Mth.floor(area.maxX));
        int minZ = SectionPos.blockToSectionCoord(Mth.floor(area.minZ));
        int maxZ = SectionPos.blockToSectionCoord(Mth.floor(area.maxZ));
        if ((long) (maxX - minX + 1) * (maxZ - minZ + 1) > 64) return List.of();
        List<Beam> found = null;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                List<Beam> bucket = buckets.get(ChunkPos.asLong(x, z));
                if (bucket == null) continue;
                for (Beam beam : bucket) {
                    if (!beam.bounds().intersects(area)) continue;
                    if (found == null) found = new ArrayList<>();
                    if (!found.contains(beam)) found.add(beam);
                }
            }
        }
        return found == null ? List.of() : found;
    }

    @Nullable
    public static List<VoxelShape> collect(Level level, AABB area) {
        if (!enabled()) return null;
        BeamCollisionIndex index = of(level);
        if (index.buckets.isEmpty()) return null;
        List<VoxelShape> collected = null;
        for (Beam beam : index.near(area)) {
            for (int i = 0; i < beam.boxes().length; i++) {
                if (!beam.boxes()[i].intersects(area)) continue;
                if (collected == null) collected = new ArrayList<>();
                collected.add(beam.shapes()[i]);
            }
        }
        return collected;
    }

    public static boolean blocksCell(Level level, int x, int y, int z, int width, int height) {
        if (!enabled()) return false;
        BeamCollisionIndex index = of(level);
        if (index.buckets.isEmpty()) return false;
        AABB cells = new AABB(x, y + STEP_CLEARANCE, z, x + width, y + height, z + width);
        for (Beam beam : index.near(cells)) {
            for (AABB box : beam.boxes()) {
                if (box.intersects(cells)) return true;
            }
        }
        return false;
    }

    public static boolean obstructsPlacement(Level level, BlockPos pos, VoxelShape shape) {
        if (!enabled() || shape.isEmpty()) return false;
        BeamCollisionIndex index = of(level);
        if (index.buckets.isEmpty()) return false;
        List<AABB> parts = interiors(shape.move(pos.getX(), pos.getY(), pos.getZ()));
        AABB area = shape.bounds().move(pos);
        AABB reach = area.inflate(HALF_WIDTH);
        SolidLookup solids = new SolidLookup(level);
        for (Beam beam : index.near(area)) {
            for (AABB interior : parts) {
                if (interior.contains(beam.start()) || interior.contains(beam.end()) || interior.clip(beam.start(), beam.end()).isPresent()) {
                    return true;
                }
            }
            if (sidesBlocked(beam.start(), beam.end(), reach, p -> contains(parts, p) || solids.inside(p), p -> contains(parts, p))) return true;
        }
        return false;
    }

    public static boolean obstructedBetween(Level level, Vec3 start, Vec3 end) {
        if (!enabled() || start.distanceToSqr(end) < END_TRIM * END_TRIM) return false;
        if (level.isClientSide) {
            Memo memo = lastClientCheck;
            if (memo != null && memo.level().get() == level && memo.time() == level.getGameTime() && memo.start().equals(start) && memo.end().equals(end)) return memo.result();
        }
        SolidLookup solids = new SolidLookup(level);
        Boolean centre = BlockGetter.traverseBlocks(start, end, solids, (lookup, pos) -> {
            for (AABB interior : lookup.interiorsAt(pos)) {
                if (interior.contains(start) || interior.contains(end) || interior.clip(start, end).isPresent()) return Boolean.TRUE;
            }
            return null;
        }, lookup -> Boolean.FALSE);
        boolean result = centre || sidesBlocked(start, end, null, solids::inside, null);
        if (level.isClientSide) lastClientCheck = new Memo(new WeakReference<>(level), level.getGameTime(), start, end, result);
        return result;
    }

    private static boolean sidesBlocked(Vec3 start, Vec3 end, @Nullable AABB near, Predicate<Vec3> solid, @Nullable Predicate<Vec3> required) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length < END_TRIM * 4) return false;
        Vec3 axis = delta.scale(1.0 / length);
        Vec3 helper = Math.abs(axis.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 u = axis.cross(helper).normalize().scale(SIDE_OFFSET);
        Vec3 v = axis.cross(u).normalize().scale(SIDE_OFFSET);
        int samples = Math.max(1, Mth.ceil((length - END_TRIM * 2) / SAMPLE_STEP));
        for (int i = 0; i <= samples; i++) {
            Vec3 centre = start.add(axis.scale(END_TRIM + (length - END_TRIM * 2) * i / samples));
            if (near != null && !near.contains(centre)) continue;
            if (opposite(centre.add(u), centre.subtract(u), solid, required) || opposite(centre.add(v), centre.subtract(v), solid, required)) return true;
        }
        return false;
    }

    private static boolean opposite(Vec3 a, Vec3 b, Predicate<Vec3> solid, @Nullable Predicate<Vec3> required) {
        if (required != null && !required.test(a) && !required.test(b)) return false;
        return solid.test(a) && solid.test(b);
    }

    private static boolean contains(List<AABB> boxes, Vec3 point) {
        for (AABB box : boxes) {
            if (box.contains(point)) return true;
        }
        return false;
    }

    private static List<AABB> interiors(VoxelShape shape) {
        List<AABB> boxes = new ArrayList<>();
        for (AABB part : shape.toAabbs()) boxes.add(part.deflate(INTERIOR_EPSILON));
        return boxes;
    }

    private record Memo(WeakReference<Level> level, long time, Vec3 start, Vec3 end, boolean result) {
    }

    private static final class SolidLookup {
        private final Level level;
        private final Long2ObjectMap<List<AABB>> cache = new Long2ObjectOpenHashMap<>();

        SolidLookup(Level level) {
            this.level = level;
        }

        List<AABB> interiorsAt(BlockPos pos) {
            return cache.computeIfAbsent(pos.asLong(), key -> {
                BlockState state = level.getBlockState(pos);
                if (state.isAir() || BeamSources.isAnchor(state)) return List.of();
                VoxelShape shape = state.getCollisionShape(level, pos);
                if (shape.isEmpty() && !state.canBeReplaced()) shape = state.getShape(level, pos);
                if (shape.isEmpty()) return List.of();
                return interiors(shape.move(pos.getX(), pos.getY(), pos.getZ()));
            });
        }

        boolean inside(Vec3 point) {
            return contains(interiorsAt(BlockPos.containing(point)), point);
        }
    }
}
