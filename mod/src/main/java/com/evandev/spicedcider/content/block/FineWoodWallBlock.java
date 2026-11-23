package com.evandev.spicedcider.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;

public class FineWoodWallBlock extends WallBlock {
    private final Map<BlockState, VoxelShape> shapes = new HashMap<>();
    private final Map<BlockState, VoxelShape> collisionShapes = new HashMap<>();

    public FineWoodWallBlock(Properties properties) {
        super(properties);
        for (BlockState state : getStateDefinition().getPossibleStates()) {
            shapes.put(state, shape(state, 16.0));
            collisionShapes.put(state, shape(state, 24.0));
        }
    }

    private static VoxelShape shape(BlockState state, double height) {
        VoxelShape shape = Block.box(4.0, 0.0, 4.0, 12.0, height, 12.0);
        if (state.getValue(NORTH_WALL) != WallSide.NONE) shape = Shapes.or(shape, Block.box(4.0, 0.0, 0.0, 12.0, height, 4.0));
        if (state.getValue(SOUTH_WALL) != WallSide.NONE) shape = Shapes.or(shape, Block.box(4.0, 0.0, 12.0, 12.0, height, 16.0));
        if (state.getValue(WEST_WALL) != WallSide.NONE) shape = Shapes.or(shape, Block.box(0.0, 0.0, 4.0, 4.0, height, 12.0));
        if (state.getValue(EAST_WALL) != WallSide.NONE) shape = Shapes.or(shape, Block.box(12.0, 0.0, 4.0, 16.0, height, 12.0));
        return shape;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return collisionShapes.get(state);
    }
}
