package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContents.Mutable.class)
public class BundleNoNestingMixin {

    @ModifyReturnValue(method = "getMaxAmountToAdd", at = @At("RETURN"))
    private int spicedcider$noBundlesInBundles(int amount, ItemStack stack) {
        if (amount > 0 && stack.has(DataComponents.BUNDLE_CONTENTS) && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.noNestedBundles, false)) {
            return 0;
        }
        return amount;
    }
}
