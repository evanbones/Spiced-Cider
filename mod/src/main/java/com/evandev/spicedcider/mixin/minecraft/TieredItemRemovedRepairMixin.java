package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.util.RemovedRepairItems;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TieredItem.class)
public class TieredItemRemovedRepairMixin {

    @Shadow
    @Final
    private Tier tier;

    @ModifyReturnValue(method = "isValidRepairItem", at = @At("RETURN"))
    private boolean spicedcider$acceptReplacementRepairItem(boolean original, ItemStack toRepair, ItemStack repair) {
        return original || RemovedRepairItems.accepts(tier.getRepairIngredient(), repair);
    }
}
