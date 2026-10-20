package com.evandev.spicedcider.content.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CeramicMugItem extends Item {
    private static final ResourceLocation WATER_CUP = ResourceLocation.fromNamespaceAndPath("bountifulfares", "water_cup");

    public CeramicMugItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Optional<Item> waterCup = BuiltInRegistries.ITEM.getOptional(WATER_CUP);
        if (waterCup.isEmpty()) return InteractionResultHolder.pass(stack);

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(stack);

        BlockPos pos = hit.getBlockPos();
        if (!level.mayInteract(player, pos) || !level.getFluidState(pos).is(FluidTags.WATER)) {
            return InteractionResultHolder.pass(stack);
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        player.awardStat(Stats.ITEM_USED.get(this));
        ItemStack filled = ItemUtils.createFilledResult(stack, player, new ItemStack(waterCup.get()));
        return InteractionResultHolder.sidedSuccess(filled, level.isClientSide());
    }
}
