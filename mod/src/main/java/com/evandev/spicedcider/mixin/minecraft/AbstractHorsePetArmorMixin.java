package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.util.PetArmorUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class AbstractHorsePetArmorMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void cider$repairBodyArmor(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!SpicedCiderConfig.STARTUP.unifiedPetArmor.get()) return;

        AbstractHorse horse = (AbstractHorse) (Object) this;
        if (horse.isVehicle() || horse.isBaby() || !horse.isTamed() || player.isSecondaryUseActive()) return;

        ItemStack armor = horse.getBodyArmorItem();
        if (!PetArmorUtil.isPetArmor(armor) || !armor.isDamaged()) return;

        ItemStack held = player.getItemInHand(hand);
        if (!PetArmorUtil.isRepairMaterial(armor, held)) return;

        if (!horse.level().isClientSide) {
            PetArmorUtil.repair(horse, held);
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(horse.level().isClientSide()));
    }
}
