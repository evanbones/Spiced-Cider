package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.registry.ModTreeFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SpindlyTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<SpindlyTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> trunkPlacerParts(instance)
            .and(IntProvider.codec(0, 8).fieldOf("branch_count").forGetter(placer -> placer.branchCount))
            .and(IntProvider.codec(1, 4).fieldOf("branch_length").forGetter(placer -> placer.branchLength))
            .and(Codec.intRange(1, 32).fieldOf("min_height_for_branches").forGetter(placer -> placer.minHeightForBranches))
            .apply(instance, SpindlyTrunkPlacer::new));

    private final IntProvider branchCount;
    private final IntProvider branchLength;
    private final int minHeightForBranches;

    public SpindlyTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider branchCount, IntProvider branchLength, int minHeightForBranches) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchCount = branchCount;
        this.branchLength = branchLength;
        this.minHeightForBranches = minHeightForBranches;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTreeFeatures.SPINDLY_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                                            int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        setDirtAt(level, blockSetter, random, pos.below(), config);

        for (int i = 0; i < freeTreeHeight; i++) {
            this.placeLog(level, blockSetter, random, pos.above(i), config);
        }

        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        attachments.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));

        int lowest = Math.max(this.minHeightForBranches, freeTreeHeight / 2);
        int highest = freeTreeHeight - 3;
        if (highest < lowest) return attachments;

        int branches = this.branchCount.sample(random);
        List<Integer> heights = new ArrayList<>();
        for (int y = lowest; y <= highest; y += 2) heights.add(y);

        Direction previous = null;
        for (int b = 0; b < branches && !heights.isEmpty(); b++) {
            int height = heights.remove(random.nextInt(heights.size()));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            if (direction == previous) direction = direction.getClockWise();
            previous = direction;

            BlockPos.MutableBlockPos cursor = pos.above(height).mutable();
            Direction.Axis axis = direction.getAxis();
            int length = this.branchLength.sample(random);
            for (int i = 0; i < length; i++) {
                cursor.move(direction);
                this.placeLog(level, blockSetter, random, cursor, config, state -> state.trySetValue(RotatedPillarBlock.AXIS, axis));
            }
            cursor.move(Direction.UP);
            this.placeLog(level, blockSetter, random, cursor, config);
            attachments.add(new FoliagePlacer.FoliageAttachment(cursor.above().immutable(), -1, false));
        }

        return attachments;
    }
}
