package com.evandev.spicedcider.compat.create.deco;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CatwalkRailingBlock extends Block implements IWrenchable, ProperWaterloggedBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 14, 2);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 14, 16, 14, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(14, 0, 0, 16, 14, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 0, 0, 2, 14, 16);

    public CatwalkRailingBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(NORTH, true).setValue(SOUTH, false).setValue(EAST, false)
                .setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    public static boolean isRailing(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof CatwalkRailingBlock;
    }

    public static BooleanProperty sideProperty(Direction direction) {
        return switch (direction) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> NORTH;
        };
    }

    public static int sideCount(BlockState state) {
        int count = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (state.getValue(sideProperty(direction))) count++;
        }
        return count;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        return withWater(defaultBlockState().setValue(NORTH, facing == Direction.NORTH).setValue(SOUTH, facing == Direction.SOUTH)
                .setValue(EAST, facing == Direction.EAST).setValue(WEST, facing == Direction.WEST), context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        if (state.getValue(NORTH)) shape = Shapes.or(shape, SHAPE_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(EAST)) shape = Shapes.or(shape, SHAPE_EAST);
        if (state.getValue(WEST)) shape = Shapes.or(shape, SHAPE_WEST);
        return shape;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        updateWater(level, state, pos);
        return state;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Player player = context.getPlayer();
        if (level.isClientSide() || face == Direction.DOWN) return InteractionResult.PASS;
        if (sideCount(state) <= 1) return IWrenchable.super.onSneakWrenched(state, context);

        Vec3 local = context.getClickLocation().subtract(pos.getCenter());
        Direction side;
        if (face == Direction.UP) {
            boolean bottomLeft = local.x < -local.z;
            boolean topLeft = local.x < local.z;
            side = bottomLeft ? (topLeft ? Direction.WEST : Direction.NORTH) : (topLeft ? Direction.SOUTH : Direction.EAST);
        } else {
            side = state.getValue(sideProperty(face)) && Math.abs(local.get(face.getAxis())) > 0.45 ? face : face.getOpposite();
        }
        if (!state.getValue(sideProperty(side))) return InteractionResult.PASS;
        level.setBlock(pos, state.setValue(sideProperty(side), false), Block.UPDATE_ALL);
        IWrenchable.playRemoveSound(level, pos);
        if (player != null && !player.getAbilities().instabuild) {
            player.getInventory().placeItemBackInInventory(new ItemStack(this));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getRotatedBlockState(BlockState state, Direction targetedFace) {
        return targetedFace.getAxis() == Direction.Axis.Y ? rotate(state, Rotation.CLOCKWISE_90) : state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState rotated = state;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            rotated = rotated.setValue(sideProperty(rotation.rotate(direction)), state.getValue(sideProperty(direction)));
        }
        return rotated;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState mirrored = state;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            mirrored = mirrored.setValue(sideProperty(mirror.mirror(direction)), state.getValue(sideProperty(direction)));
        }
        return mirrored;
    }
}
