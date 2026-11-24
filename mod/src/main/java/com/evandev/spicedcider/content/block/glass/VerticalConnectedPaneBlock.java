package com.evandev.spicedcider.content.block.glass;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class VerticalConnectedPaneBlock extends LeadedGlassPaneBlock {
    public static final MapCodec<VerticalConnectedPaneBlock> CODEC = simpleCodec(VerticalConnectedPaneBlock::new);
    public static final IntegerProperty VERTICAL_POSITION = IntegerProperty.create("vertical_position", 0, 3);

    public VerticalConnectedPaneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends VerticalConnectedPaneBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VERTICAL_POSITION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(VERTICAL_POSITION, this.position(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        BlockState updated = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (!updated.is(this)) return updated;
        return updated.setValue(VERTICAL_POSITION, this.position(updated, level, pos));
    }

    private int position(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());
        Direction axis = PaneConnections.fullPaneAxis(state);
        if (axis == null) {
            return PaneConnections.vertical(PaneConnections.sameConnections(this, state, above), PaneConnections.sameConnections(this, state, below));
        }
        return PaneConnections.vertical(PaneConnections.continues(this, above, axis), PaneConnections.continues(this, below, axis));
    }
}
