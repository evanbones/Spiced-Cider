package com.evandev.spicedcider.content.drawer;

import com.evandev.spicedcider.registry.ModDataComponents;
import com.evandev.spicedcider.registry.ModTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDrawerBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<ItemDrawerBlock> CODEC = simpleCodec(ItemDrawerBlock::new);
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    public ItemDrawerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LOCKED, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LOCKED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ItemDrawerBlockEntity drawer && drawer.isLocked() != state.getValue(LOCKED)) {
            level.setBlock(pos, state.setValue(LOCKED, drawer.isLocked()), 3);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemDrawerBlockEntity(pos, state);
    }

    public static boolean isFront(BlockState state, Direction side) {
        return state.hasProperty(FACING) && state.getValue(FACING) == side;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || !isFront(state, hit.getDirection())) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ItemDrawerBlockEntity drawer)) return ItemInteractionResult.FAIL;
        if (stack.is(ModTags.Items.DRAWER_KEYS)) {
            drawer.toggleLock();
            level.playSound(null, pos, drawer.isLocked() ? SoundEvents.IRON_TRAPDOOR_CLOSE : SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.5f, 1.4f);
            player.displayClientMessage(Component.translatable(drawer.isLocked() ? "message.spicedcider.item_drawer.locked" : "message.spicedcider.item_drawer.unlocked"), true);
            return ItemInteractionResult.SUCCESS;
        }
        drawer.putFromPlayer(player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!isFront(state, hit.getDirection())) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof ItemDrawerBlockEntity drawer) drawer.putFromPlayer(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide || player.isCreative()) return;
        BlockHitResult hit = rayTrace(level, player);
        if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(pos) && isFront(state, hit.getDirection())) {
            takeFromFront(level, pos, state, player);
        }
    }

    public static void takeFromFront(Level level, BlockPos pos, BlockState state, Player player) {
        if (!(level.getBlockEntity(pos) instanceof ItemDrawerBlockEntity drawer) || drawer.isEmpty()) return;
        int amount = player.isShiftKeyDown() ? drawer.getItem().getMaxStackSize() : 1;
        ItemStack taken = drawer.extract(amount, false);
        if (taken.isEmpty()) return;
        if (player.getInventory().add(taken)) {
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f,
                    ((level.random.nextFloat() - level.random.nextFloat()) * 0.7f + 1) * 2);
        } else {
            BlockPos out = pos.relative(state.getValue(FACING));
            ItemEntity entity = new ItemEntity(level, out.getX() + 0.5, out.getY() + 0.3, out.getZ() + 0.5, taken);
            entity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(entity);
        }
    }

    public static BlockHitResult rayTrace(Level level, Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1).scale(player.blockInteractionRange() + 1));
        return level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ItemDrawerBlockEntity drawer ? drawer.getComparatorSignal() : 0;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        DrawerContents contents = stack.get(ModDataComponents.DRAWER_CONTENTS.get());
        if (contents == null || contents.item().isEmpty()) return;
        if (contents.count() > 0) {
            tooltip.add(Component.translatable("tooltip.spicedcider.item_drawer.contents", contents.count(), contents.item().getHoverName()).withStyle(ChatFormatting.GRAY));
        }
        if (contents.locked()) {
            tooltip.add(Component.translatable("tooltip.spicedcider.item_drawer.locked", contents.item().getHoverName()).withStyle(ChatFormatting.GRAY));
        }
    }
}
