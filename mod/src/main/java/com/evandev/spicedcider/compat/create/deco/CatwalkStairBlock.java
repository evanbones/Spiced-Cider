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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class CatwalkStairBlock extends Block implements IWrenchable, ProperWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty RAILING_LEFT = BooleanProperty.create("railing_left");
    public static final BooleanProperty RAILING_RIGHT = BooleanProperty.create("railing_right");

    private static final VoxelShape BOX_NORTH = Shapes.or(Block.box(0, 14, 8, 16, 16, 16), Block.box(0, 6, 0, 16, 8, 8));
    private static final VoxelShape RAILING_WEST = Shapes.or(Block.box(14, 0, 0, 16, 20, 4), Block.box(14, 4, 4, 16, 24, 8),
            Block.box(14, 8, 8, 16, 28, 12), Block.box(14, 12, 12, 16, 32, 16));
    private static final VoxelShape RAILING_EAST = Shapes.or(Block.box(0, 0, 0, 2, 20, 4), Block.box(0, 4, 4, 2, 24, 8),
            Block.box(0, 8, 8, 2, 28, 12), Block.box(0, 12, 12, 2, 32, 16));
    private static final VoxelShape[] SHAPES = new VoxelShape[16];

    static {
        VoxelShape[] north = {BOX_NORTH, Shapes.or(BOX_NORTH, RAILING_WEST), Shapes.or(BOX_NORTH, RAILING_EAST), Shapes.or(BOX_NORTH, RAILING_WEST, RAILING_EAST)};
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (int d = 0; d < 4; d++) {
            for (int r = 0; r < 4; r++) {
                SHAPES[d * 4 + r] = rotate(north[r], dirs[d]);
            }
        }
    }

    private final Supplier<? extends Block> railing;

    public CatwalkStairBlock(Properties properties, Supplier<? extends Block> railing) {
        super(properties);
        this.railing = railing;
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false)
                .setValue(RAILING_LEFT, false).setValue(RAILING_RIGHT, false));
    }

    public Block getRailing() {
        return railing.get();
    }

    public static boolean isCatwalkStair(ItemStack stack) {
        return stack.getItem() instanceof BlockItem item && item.getBlock() instanceof CatwalkStairBlock;
    }

    public static boolean isLeftSide(BlockState state, Vec3 local) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> local.x < 0;
            case EAST -> local.z > 0;
            case WEST -> local.z < 0;
            default -> local.x > 0;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return withWater(defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()), context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, RAILING_LEFT, RAILING_RIGHT);
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = switch (state.getValue(FACING)) {
            case SOUTH -> 4;
            case EAST -> 8;
            case WEST -> 12;
            default -> 0;
        };
        if (state.getValue(RAILING_LEFT)) index += 1;
        if (state.getValue(RAILING_RIGHT)) index += 2;
        return SHAPES[index];
    }

    private static VoxelShape rotate(VoxelShape input, Direction to) {
        VoxelShape shape = input;
        int turns = (to.get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < turns; i++) {
            VoxelShape[] next = {Shapes.empty()};
            shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    next[0] = Shapes.or(next[0], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            shape = next[0];
        }
        return shape;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (state.getValue(RAILING_LEFT) || state.getValue(RAILING_RIGHT)) {
            BooleanProperty side = isLeftSide(state, context.getClickLocation().subtract(pos.getCenter())) ? RAILING_LEFT : RAILING_RIGHT;
            if (level.isClientSide() || !state.getValue(side)) return InteractionResult.PASS;
            level.setBlock(pos, state.setValue(side, false), Block.UPDATE_ALL);
            if (player != null && !player.getAbilities().instabuild) {
                player.getInventory().placeItemBackInInventory(new ItemStack(getRailing()));
            }
            IWrenchable.playRemoveSound(level, pos);
            return InteractionResult.SUCCESS;
        }
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
