package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrucibleItem extends BlockItem {
    public static final int POUR_RATE = 50;
    public static final int POUR_INTERVAL = 2;

    public CrucibleItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static FluidStack fluid(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FLUID_CONTENTS.get(), SimpleFluidContent.EMPTY).copy();
    }

    private static void setFluid(ItemStack stack, FluidStack fluid) {
        if (fluid.isEmpty()) {
            stack.remove(ModDataComponents.FLUID_CONTENTS.get());
        } else {
            stack.set(ModDataComponents.FLUID_CONTENTS.get(), SimpleFluidContent.copyOf(fluid));
        }
    }

    @Nullable
    private static IFluidHandler target(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        FluidStack fluid = fluid(context.getItemInHand());
        Player player = context.getPlayer();
        if (!fluid.isEmpty() && player != null && !player.isSecondaryUseActive()) {
            IFluidHandler handler = target(context.getLevel(), context.getClickedPos(), context.getClickedFace());
            if (handler != null && handler.fill(fluid.copyWithAmount(Math.min(POUR_RATE, fluid.getAmount())), IFluidHandler.FluidAction.SIMULATE) > 0) {
                player.startUsingItem(context.getHand());
                context.getLevel().playSound(player, context.getClickedPos(), SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 0.6F, 1.2F);
                return InteractionResult.CONSUME;
            }
        }
        return super.useOn(context);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide() || (72000 - remaining) % POUR_INTERVAL != 0) {
            return;
        }
        FluidStack fluid = fluid(stack);
        BlockHitResult hit = pick(level, entity);
        IFluidHandler handler = fluid.isEmpty() || hit == null ? null : target(level, hit.getBlockPos(), hit.getDirection());
        if (handler == null) {
            entity.stopUsingItem();
            return;
        }
        int filled = handler.fill(fluid.copyWithAmount(Math.min(POUR_RATE, fluid.getAmount())), IFluidHandler.FluidAction.EXECUTE);
        if (filled <= 0) {
            entity.stopUsingItem();
            return;
        }
        fluid.shrink(filled);
        setFluid(stack, fluid);
        if (level instanceof ServerLevel server) {
            Vec3 from = entity.getEyePosition().add(entity.getLookAngle().scale(0.6)).add(0.0, -0.35, 0.0);
            Vec3 to = hit.getLocation();
            for (int i = 0; i < 3; i++) {
                double t = level.random.nextDouble();
                server.sendParticles(ParticleTypes.FALLING_LAVA, from.x + (to.x - from.x) * t, from.y + (to.y - from.y) * t, from.z + (to.z - from.z) * t, 1, 0.02, 0.02, 0.02, 0.0);
            }
            if (level.random.nextInt(4) == 0) {
                level.playSound(null, hit.getBlockPos(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.4F, 1.1F + level.random.nextFloat() * 0.3F);
            }
        }
        if (fluid.isEmpty()) {
            entity.stopUsingItem();
        }
    }

    @Nullable
    private static BlockHitResult pick(Level level, LivingEntity entity) {
        Vec3 eye = entity.getEyePosition();
        Vec3 end = eye.add(entity.getLookAngle().scale(5.0));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
        return hit.getType() == HitResult.Type.BLOCK ? hit : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        FluidStack fluid = fluid(stack);
        if (!fluid.isEmpty()) {
            tooltip.add(Component.translatable("spicedcider.crucible.fluid", fluid.getAmount(), fluid.getHoverName()).withStyle(ChatFormatting.GOLD));
        }
        stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()
                .forEach(item -> tooltip.add(item.getHoverName().copy().withStyle(ChatFormatting.GRAY)));
    }
}
