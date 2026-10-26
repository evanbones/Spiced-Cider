package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.registry.ModTreeFeatures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
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

public class CacaoTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<CacaoTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> trunkPlacerParts(instance)
            .and(IntProvider.codec(1, 4).fieldOf("branch_count").forGetter(placer -> placer.branchCount))
            .and(IntProvider.codec(1, 5).fieldOf("branch_length").forGetter(placer -> placer.branchLength))
            .apply(instance, CacaoTrunkPlacer::new));

    private final IntProvider branchCount;
    private final IntProvider branchLength;

    public CacaoTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider branchCount, IntProvider branchLength) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchCount = branchCount;
        this.branchLength = branchLength;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTreeFeatures.CACAO_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                                            int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        setDirtAt(level, blockSetter, random, pos.below(), config);

        for (int i = 0; i < freeTreeHeight; i++) {
            this.placeLog(level, blockSetter, random, pos.above(i), config);
        }

        BlockPos top = pos.above(freeTreeHeight - 1);
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        attachments.add(new FoliagePlacer.FoliageAttachment(top.above(), -1, false));

        List<Direction> directions = Util.shuffledCopy(Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new), random);
        int branches = Math.min(this.branchCount.sample(random), directions.size());
        for (int b = 0; b < branches; b++) {
            Direction direction = directions.get(b);
            Direction.Axis axis = direction.getAxis();
            int length = this.branchLength.sample(random);
            int rise = random.nextInt(length);
            BlockPos.MutableBlockPos cursor = top.below(random.nextInt(2)).mutable();
            for (int i = 0; i < length; i++) {
                cursor.move(direction);
                this.placeLog(level, blockSetter, random, cursor, config, state -> state.trySetValue(RotatedPillarBlock.AXIS, axis));
                if (i == rise) {
                    cursor.move(Direction.UP);
                    this.placeLog(level, blockSetter, random, cursor, config);
                }
            }
            attachments.add(new FoliagePlacer.FoliageAttachment(cursor.above().immutable(), 0, false));
        }

        return attachments;
    }
}
