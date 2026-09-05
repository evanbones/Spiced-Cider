package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.util.PetArmorUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfPetArmorMixin {

    @WrapOperation(
            method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private boolean cider$acceptAnyPetArmor(ItemStack stack, Item item, Operation<Boolean> original) {
        if (item == Items.WOLF_ARMOR && SpicedCiderConfig.COMMON.wolvesWearAnyArmor.get()) {
            return PetArmorUtil.isPetArmor(stack);
        }
        return original.call(stack, item);
    }

    @WrapOperation(
            method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;test(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private boolean cider$repairWithOwnMaterial(Ingredient ingredient, ItemStack held, Operation<Boolean> original) {
        ItemStack armor = ((Wolf) (Object) this).getBodyArmorItem();
        if (PetArmorUtil.isPetArmor(armor)) {
            return PetArmorUtil.isRepairMaterial(armor, held);
        }
        return original.call(ingredient, held);
    }

    @Inject(method = "hasArmor", at = @At("HEAD"), cancellable = true)
    private void cider$hasArmor(CallbackInfoReturnable<Boolean> cir) {
        if (!SpicedCiderConfig.COMMON.wolvesWearAnyArmor.get()) return;

        if (PetArmorUtil.isPetArmor(((Wolf) (Object) this).getBodyArmorItem())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void cider$requireDamageableArmor(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!((Wolf) (Object) this).getBodyArmorItem().isDamageableItem()) {
            cir.setReturnValue(false);
        }
    }
}
