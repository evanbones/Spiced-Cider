package com.evandev.spicedcider.mixin.tide;

import com.evandev.spicedcider.content.handler.GrapplingHookHandler;
import com.evandev.spicedcider.registry.ModItems;
import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.items.TideFishingRodItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TideFishingRodItem.class, remap = false)
public class TideFishingRodItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack rod = player.getItemInHand(hand);
        ItemStack hookStack = CustomRodManager.getHook(rod);
        if (hookStack.is(ModItems.GRAPPLING_HOOK.get()) || hookStack.is(ModItems.STICKY_GRAPPLING_HOOK.get())) {
            cir.setReturnValue(GrapplingHookHandler.useGrapplingHook(level, player, hand, rod, hookStack));
        }
    }
}
