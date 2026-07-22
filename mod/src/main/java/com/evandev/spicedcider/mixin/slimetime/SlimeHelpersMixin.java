package com.evandev.spicedcider.mixin.slimetime;

import cc.cassian.slime.util.SlimeHelpers;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SlimeHelpers.class, remap = false)
public class SlimeHelpersMixin {

    @Inject(method = "mergeSlimeBalls", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$disableMergeSlimeBalls(CallbackInfoReturnable<Boolean> cir) {
        if (SpicedCiderConfig.COMMON.slimeTimeDisableItemMerging.get()) {
            cir.setReturnValue(false);
        }
    }
}
