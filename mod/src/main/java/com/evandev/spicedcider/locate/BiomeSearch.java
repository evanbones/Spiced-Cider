package com.evandev.spicedcider.locate;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class BiomeSearch {
    private static final int FIRST_BATCH = 64;
    private static final int MAX_BATCH = 1 << 15;
    private static final int MIN_SLICE = 16;

    public static Pair<BlockPos, Holder<Biome>> findClosest(BiomeSource source, BlockPos pos, int radius, int horizontalStep, int verticalStep,
                                                           Predicate<Holder<Biome>> predicate, Climate.Sampler sampler, LevelReader level) {
        Set<Holder<Biome>> targets = source.possibleBiomes().stream().filter(predicate).collect(Collectors.toUnmodifiableSet());
        if (targets.isEmpty()) return null;
        int steps = Math.floorDiv(radius, horizontalStep);
        int[] heights = Mth.outFromOrigin(pos.getY(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), verticalStep).toArray();
        ColumnClimate climate = ColumnClimate.of(sampler, level);
        Iterator<BlockPos.MutableBlockPos> spiral = BlockPos.spiralAround(BlockPos.ZERO, steps, Direction.EAST, Direction.SOUTH).iterator();
        int[] xs = new int[MAX_BATCH];
        int[] zs = new int[MAX_BATCH];
        int[] hitY = new int[MAX_BATCH];
        @SuppressWarnings("unchecked")
        Holder<Biome>[] hitBiome = new Holder[MAX_BATCH];
        int batch = FIRST_BATCH;
        while (spiral.hasNext()) {
            int count = 0;
            while (count < batch && spiral.hasNext()) {
                BlockPos.MutableBlockPos offset = spiral.next();
                xs[count] = pos.getX() + offset.getX() * horizontalStep;
                zs[count] = pos.getZ() + offset.getZ() * horizontalStep;
                count++;
            }
            AtomicInteger first = new AtomicInteger(count);
            LocateWorkers.forEachSlice(count, MIN_SLICE, (from, to) -> {
                Climate.Sampler local = climate.newSampler();
                for (int i = from; i < to && i < first.get(); i++) {
                    int qx = QuartPos.fromBlock(xs[i]);
                    int qz = QuartPos.fromBlock(zs[i]);
                    for (int y : heights) {
                        Holder<Biome> biome = source.getNoiseBiome(qx, QuartPos.fromBlock(y), qz, local);
                        if (targets.contains(biome)) {
                            hitY[i] = y;
                            hitBiome[i] = biome;
                            first.accumulateAndGet(i, Math::min);
                            break;
                        }
                    }
                }
            });
            int found = first.get();
            if (found < count) return Pair.of(new BlockPos(xs[found], hitY[found], zs[found]), hitBiome[found]);
            batch = Math.min(batch * 2, MAX_BATCH);
        }
        return null;
    }
}
