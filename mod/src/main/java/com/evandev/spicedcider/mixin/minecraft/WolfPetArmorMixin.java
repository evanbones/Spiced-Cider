package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfPetArmorMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void cider$equipAnyPetArmor(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        ItemStack stack = player.getItemInHand(hand);

        if (wolf.isTame() && wolf.isOwnedBy(player) && !wolf.isBaby() && !wolf.hasArmor()) {
            if (stack.getItem() instanceof AnimalArmorItem) {
                wolf.setBodyArmorItem(stack.copyWithCount(1));
                stack.consume(1, player);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "hasArmor", at = @At("HEAD"), cancellable = true)
    private void cider$hasArmor(CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getBodyArmorItem().getItem() instanceof AnimalArmorItem) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void cider$canArmorAbsorb(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getBodyArmorItem().getItem() instanceof AnimalArmorItem) {
            cir.setReturnValue(true);
        }
    }
}