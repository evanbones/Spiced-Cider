package com.evandev.spicedcider.content.block;

import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RichGravelBlock extends ColoredFallingBlock {
    public static final BooleanProperty PLACED = BooleanProperty.create("placed");

    public RichGravelBlock(ColorRGBA dustColor, Properties properties) {
        super(dustColor, properties);
        registerDefaultState(stateDefinition.any().setValue(PLACED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(PLACED, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLACED);
    }
}
