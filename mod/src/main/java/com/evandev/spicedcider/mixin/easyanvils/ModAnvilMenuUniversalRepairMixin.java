package com.evandev.spicedcider.mixin.easyanvils;

import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("easyanvils")
@Mixin(targets = "fuzs.easyanvils.world.inventory.ModAnvilMenu", remap = false)
public class ModAnvilMenuUniversalRepairMixin {

    @WrapOperation(method = "createResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isValidRepairItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean spicedcider$universalRepair(Item item, ItemStack toRepair, ItemStack repair, Operation<Boolean> original) {
        return original.call(item, toRepair, repair)
                || toRepair.isDamageableItem() && repair.is(ModTags.Items.UNIVERSAL_REPAIR_MATERIALS);
    }
}
