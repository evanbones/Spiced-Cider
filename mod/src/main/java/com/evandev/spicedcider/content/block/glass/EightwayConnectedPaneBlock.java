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

public class EightwayConnectedPaneBlock extends LeadedGlassPaneBlock {
    public static final MapCodec<EightwayConnectedPaneBlock> CODEC = simpleCodec(EightwayConnectedPaneBlock::new);
    public static final IntegerProperty EIGHTWAY_POSITION = IntegerProperty.create("eightway_position", 0, 46);

    public EightwayConnectedPaneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends EightwayConnectedPaneBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EIGHTWAY_POSITION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(EIGHTWAY_POSITION, this.position(state, context.getLevel(), context.getClickedPos()));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        BlockState updated = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (!updated.is(this)) return updated;
        return updated.setValue(EIGHTWAY_POSITION, this.position(updated, level, pos));
    }

    private int position(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        BlockState below = level.getBlockState(pos.below());
        Direction axis = PaneConnections.fullPaneAxis(state);
        if (axis == null) {
            return 12 * PaneConnections.vertical(PaneConnections.sameConnections(this, state, above), PaneConnections.sameConnections(this, state, below));
        }
        Direction leftward = axis.getOpposite();
        return PaneConnections.eightway(
                PaneConnections.continues(this, above, axis),
                PaneConnections.continues(this, level.getBlockState(pos.above().relative(axis)), axis),
                PaneConnections.continues(this, level.getBlockState(pos.relative(axis)), axis),
                PaneConnections.continues(this, level.getBlockState(pos.below().relative(axis)), axis),
                PaneConnections.continues(this, below, axis),
                PaneConnections.continues(this, level.getBlockState(pos.below().relative(leftward)), axis),
                PaneConnections.continues(this, level.getBlockState(pos.relative(leftward)), axis),
                PaneConnections.continues(this, level.getBlockState(pos.above().relative(leftward)), axis));
    }
}
