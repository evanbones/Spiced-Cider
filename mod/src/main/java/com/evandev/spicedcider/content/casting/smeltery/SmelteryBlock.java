package com.evandev.spicedcider.content.casting.smeltery;

import com.evandev.spicedcider.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SmelteryBlock extends BaseEntityBlock {
    public static final MapCodec<SmelteryBlock> CODEC = simpleCodec(SmelteryBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BACK = BooleanProperty.create("back");

    public SmelteryBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(RIGHT, false).setValue(TOP, false).setValue(BACK, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, RIGHT, TOP, BACK);
    }

    public static boolean isController(BlockState state) {
        return !state.getValue(RIGHT) && !state.getValue(TOP) && !state.getValue(BACK);
    }

    public static BlockPos part(BlockPos controller, Direction facing, boolean right, boolean top, boolean back) {
        BlockPos pos = controller;
        if (right) pos = pos.relative(facing.getClockWise());
        if (top) pos = pos.above();
        if (back) pos = pos.relative(facing.getOpposite());
        return pos;
    }

    public static BlockPos controllerPos(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos controller = pos;
        if (state.getValue(RIGHT)) controller = controller.relative(facing.getCounterClockWise());
        if (state.getValue(TOP)) controller = controller.below();
        if (state.getValue(BACK)) controller = controller.relative(facing);
        return controller;
    }

    public static List<BlockPos> parts(BlockPos controller, Direction facing) {
        List<BlockPos> list = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            list.add(part(controller, facing, (i & 1) != 0, (i & 2) != 0, (i & 4) != 0));
        }
        return list;
    }

    @Nullable
    public static SmelteryBlockEntity controller(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof SmelteryBlock)) {
            return null;
        }
        return level.getBlockEntity(controllerPos(pos, state)) instanceof SmelteryBlockEntity smeltery ? smeltery : null;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockState state = defaultBlockState().setValue(FACING, facing);
        for (BlockPos pos : parts(context.getClickedPos(), facing)) {
            if (level.isOutsideBuildHeight(pos)) {
                return null;
            }
            if (!pos.equals(context.getClickedPos()) && !level.getBlockState(pos).canBeReplaced(context)) {
                return null;
            }
            if (!level.isUnobstructed(state, pos, CollisionContext.empty())) {
                return null;
            }
        }
        return state;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }
        Direction facing = state.getValue(FACING);
        for (int i = 1; i < 8; i++) {
            boolean right = (i & 1) != 0;
            boolean top = (i & 2) != 0;
            boolean back = (i & 4) != 0;
            level.setBlock(part(pos, facing, right, top, back), state.setValue(RIGHT, right).setValue(TOP, top).setValue(BACK, back), Block.UPDATE_ALL);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative() && !isController(state)) {
            BlockPos controller = controllerPos(pos, state);
            if (level.getBlockState(controller).getBlock() == this) {
                level.destroyBlock(controller, false, player);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, movedByPiston);
            return;
        }
        if (isController(state)) {
            if (level.getBlockEntity(pos) instanceof SmelteryBlockEntity smeltery) {
                Containers.dropContents(level, pos.above(), smeltery.drops());
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
            for (BlockPos part : parts(pos, state.getValue(FACING))) {
                BlockState other = level.getBlockState(part);
                if (!part.equals(pos) && other.getBlock() == this && controllerPos(part, other).equals(pos)) {
                    level.destroyBlock(part, false);
                }
            }
            return;
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
        BlockPos controller = controllerPos(pos, state);
        BlockState main = level.getBlockState(controller);
        if (main.getBlock() == this && isController(main)) {
            level.destroyBlock(controller, true);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (FluidUtil.getFluidHandler(stack).isPresent() && controller(level, pos, state) != null
                && FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection())) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        SmelteryBlockEntity smeltery = controller(level, pos, state);
        if (smeltery == null) {
            return InteractionResult.PASS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(smeltery, smeltery.getBlockPos());
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return isController(state) ? new SmelteryBlockEntity(pos, state) : null;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() || !isController(state) ? null : createTickerHelper(type, ModBlockEntities.SMELTERY.get(), SmelteryBlockEntity::serverTick);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        SmelteryBlockEntity smeltery = controller(level, pos, state);
        return smeltery == null ? 0 : Math.round(15.0F * smeltery.metalAmount() / SmelteryBlockEntity.METAL_CAPACITY);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }
}
