package com.evandev.spicedcider.content.block;

import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class BuddingPrismarineBlock extends Block {
    private static final Direction[] DIRECTIONS = Direction.values();

    public BuddingPrismarineBlock(Properties properties) {
        super(properties);
    }

    public static boolean canClusterGrowAtState(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8;
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockPos = pos.relative(direction);
            BlockState blockState = level.getBlockState(blockPos);
            Block block = null;

            if (canClusterGrowAtState(blockState)) {
                block = ModBlocks.SMALL_PRISMARINE_BUD.get();
            } else if (blockState.is(ModBlocks.SMALL_PRISMARINE_BUD.get()) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                block = ModBlocks.MEDIUM_PRISMARINE_BUD.get();
            } else if (blockState.is(ModBlocks.MEDIUM_PRISMARINE_BUD.get()) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                block = ModBlocks.LARGE_PRISMARINE_BUD.get();
            } else if (blockState.is(ModBlocks.LARGE_PRISMARINE_BUD.get()) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                block = ModBlocks.PRISMARINE_CLUSTER.get();
            }

            if (block != null) {
                BlockState blockState2 = block.defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING, direction)
                        .setValue(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getType() == Fluids.WATER);
                level.setBlockAndUpdate(blockPos, blockState2);
            }
        }
    }
}