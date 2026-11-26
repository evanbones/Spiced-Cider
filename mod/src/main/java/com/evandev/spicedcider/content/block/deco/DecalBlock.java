package com.evandev.spicedcider.content.block.deco;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DecalBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final MapCodec<DecalBlock> CODEC = simpleCodec(DecalBlock::new);
    private static final VoxelShape CEILING = Block.box(2, 14, 2, 14, 16, 14);
    private static final VoxelShape FLOOR = Block.box(2, 0, 2, 14, 2, 14);
    private static final VoxelShape NORTH = Block.box(2, 2, 14, 14, 14, 16);
    private static final VoxelShape SOUTH = Block.box(2, 2, 0, 14, 14, 2);
    private static final VoxelShape WEST = Block.box(14, 2, 2, 16, 14, 14);
    private static final VoxelShape EAST = Block.box(0, 2, 2, 2, 14, 14);

    public DecalBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> FLOOR;
            case CEILING -> CEILING;
            case WALL -> switch (state.getValue(FACING)) {
                case EAST -> EAST;
                case WEST -> WEST;
                case SOUTH -> SOUTH;
                default -> NORTH;
            };
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }
}
