package com.evandev.spicedcider.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class TallUnderwaterBrambleBlock extends DoublePlantBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public TallUnderwaterBrambleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, true));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.UP) && state.getBlock() != this;
    }

    @Override
    public boolean canSurvive(BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        if (state.getValue(HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER) {
            BlockState blockState = level.getBlockState(pos.below());
            return blockState.is(this) && blockState.getValue(HALF) == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER;
        } else {
            FluidState fluidState = level.getFluidState(pos);
            return super.canSurvive(state, level, pos) && fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8;
        }
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
            if (fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8) {
                return state;
            }
        }
        return null;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}