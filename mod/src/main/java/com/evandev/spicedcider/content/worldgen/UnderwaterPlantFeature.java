package com.evandev.spicedcider.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class UnderwaterPlantFeature extends Feature<SimpleBlockConfiguration> {
    public UnderwaterPlantFeature() {
        super(SimpleBlockConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        BlockState state = context.config().toPlace().getState(context.random(), pos);
        if (!level.getBlockState(pos).is(Blocks.WATER) || !state.canSurvive(level, pos)) return false;
        if (state.getBlock() instanceof DoublePlantBlock) {
            if (!level.getBlockState(pos.above()).is(Blocks.WATER)) return false;
            level.setBlock(pos, state.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 2);
            level.setBlock(pos.above(), state.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 2);
            return true;
        }
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        level.setBlock(pos, state, 2);
        return true;
    }
}
