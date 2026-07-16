package com.evandev.spicedcider.mixin.sodium;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.caffeinemc.mods.sodium.client.model.light.smooth.AoFaceData")
public abstract class AoFaceDataMixin {

    @Inject(method = "calculateCornerBrightness", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$matchVanillaCornerBlend(int a, int b, int c, int d, boolean aem, boolean bem, boolean cem, boolean dem, CallbackInfoReturnable<Integer> cir) {
        if (!SpicedCiderConfig.CLIENT.sodiumLightingParityFix.get()) {
            return;
        }

        if (a == 0) {
            a = d;
        }
        if (b == 0) {
            b = d;
        }
        if (c == 0) {
            c = d;
        }

        if (aem) {
            a &= 0xFF0000;
            a |= 0xF0;
        }
        if (bem) {
            b &= 0xFF0000;
            b |= 0xF0;
        }
        if (cem) {
            c &= 0xFF0000;
            c |= 0xF0;
        }
        if (dem) {
            d &= 0xFF0000;
            d |= 0xF0;
        }

        cir.setReturnValue((a + b + c + d >> 2) & 0xFF00FF);
    }
}
