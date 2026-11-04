package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.content.item.FireStrikerItem;
import com.farcr.nomansland.common.event.BlockInteractionEvents;
import com.farcr.nomansland.common.registry.NMLTags;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("nomansland")
@Mixin(value = BlockInteractionEvents.class, remap = false)
public class FirestarterCostMixin {

    @Inject(method = "onRightClickBlock", at = @At("HEAD"))
    private static void spicedcider$captureState(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci,
                                                 @Share("before") LocalRef<BlockState> before) {
        before.set(event.getLevel().getBlockState(event.getPos()));
    }

    @Inject(method = "onRightClickBlock", at = @At("RETURN"))
    private static void spicedcider$chargeFirestarter(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci,
                                                      @Share("before") LocalRef<BlockState> before) {
        if (!event.isCanceled()) return;
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.is(NMLTags.FIRESTARTERS)) return;
        if (!stack.canPerformAction(ItemAbilities.FIRESTARTER_LIGHT) || stack.canPerformAction(ItemAbilities.SHOVEL_DOUSE)) return;
        if (event.getLevel().getBlockState(event.getPos()) == before.get()) return;

        Player player = event.getEntity();
        if (stack.getItem() instanceof FireStrikerItem) {
            stack.consume(1, player);
        } else if (stack.isDamageableItem()) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(event.getHand()));
        }
    }
}
