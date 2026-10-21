package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.entities.balloon.BalloonEntity;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BalloonItem extends Item {
    private final DyeColor color;

    public BalloonItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public static Item byColor(DyeColor color) {
        return ModItems.BALLOONS.get(color).get();
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive() || isAimingAtSomething(player)) return InteractionResultHolder.pass(stack);
        Entity target = player.getVehicle() instanceof Boat boat ? boat : player;
        if (!BalloonEntity.canHold(target)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            BalloonEntity.tieTo(target, this.color);
            stack.consume(1, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!BalloonEntity.canTieToFence(level, pos)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            BalloonEntity.tieToFence(level, pos, this.color);
            context.getItemInHand().consume(1, context.getPlayer());
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static boolean isAimingAtSomething(Player player) {
        double blockRange = player.blockInteractionRange();
        if (player.pick(blockRange, 1.0F, false).getType() != HitResult.Type.MISS) return true;
        double entityRange = player.entityInteractionRange();
        Vec3 eye = player.getEyePosition();
        Vec3 reach = player.getViewVector(1.0F).scale(entityRange);
        AABB area = player.getBoundingBox().expandTowards(reach).inflate(1.0);
        return ProjectileUtil.getEntityHitResult(player, eye, eye.add(reach), area,
                entity -> !entity.isSpectator() && entity.isPickable(), entityRange * entityRange) != null;
    }

    public static class DispenseBehavior extends DefaultDispenseItemBehavior {
        @Override
        protected @NotNull ItemStack execute(BlockSource source, @NotNull ItemStack stack) {
            ServerLevel level = source.level();
            BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
            DyeColor color = ((BalloonItem) stack.getItem()).getColor();
            List<Entity> targets = level.getEntitiesOfClass(Entity.class, new AABB(pos), BalloonEntity::canHold);
            if (!targets.isEmpty()) {
                BalloonEntity.tieTo(targets.getFirst(), color);
                stack.shrink(1);
                return stack;
            }
            if (BalloonEntity.canTieToFence(level, pos)) {
                BalloonEntity.tieToFence(level, pos, color);
                stack.shrink(1);
                return stack;
            }
            BlockState state = level.getBlockState(pos);
            if (state.canBeReplaced() && state.getFluidState().isEmpty()) {
                BalloonEntity.releaseAt(level, Vec3.atBottomCenterOf(pos), color);
                stack.shrink(1);
                return stack;
            }
            return super.execute(source, stack);
        }
    }
}
