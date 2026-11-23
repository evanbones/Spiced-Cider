package com.evandev.spicedcider.mixin.easyanvils;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("easyanvils")
@Mixin(targets = "fuzs.easyanvils.world.inventory.ModAnvilMenu", remap = false)
public class ModAnvilMenuRepairCostMixin {

    @ModifyExpressionValue(method = "createResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Ljava/lang/String;)V", at = @At(value = "FIELD", target = "Lfuzs/easyanvils/config/ServerConfig$CostsConfig;repairWithMaterialRestoredDurability:D"))
    private double spicedcider$toolRepairCost(double original) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.toolRepairMaterialCounts, false)) {
            return original;
        }
        ItemStack stack = ((AnvilMenu) (Object) this).getSlot(0).getItem();
        int max = stack.getMaxDamage();
        if (max <= 0) {
            return original;
        }
        int count;
        if (stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.AXES)) {
            count = 3;
        } else if (stack.is(ItemTags.SWORDS) || stack.is(ItemTags.HOES)) {
            count = 2;
        } else if (stack.is(ItemTags.SHOVELS)) {
            count = 1;
        } else {
            return original;
        }
        return (Math.ceil(max / (double) count) + 0.5) / max;
    }
}
