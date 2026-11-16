package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.util.RemovedRepairItems;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorItem.class)
public class ArmorItemRemovedRepairMixin {

    @Shadow
    @Final
    protected Holder<ArmorMaterial> material;

    @ModifyReturnValue(method = "isValidRepairItem", at = @At("RETURN"))
    private boolean spicedcider$acceptReplacementRepairItem(boolean original, ItemStack toRepair, ItemStack repair) {
        return original || RemovedRepairItems.accepts(material.value().repairIngredient().get(), repair);
    }
}
