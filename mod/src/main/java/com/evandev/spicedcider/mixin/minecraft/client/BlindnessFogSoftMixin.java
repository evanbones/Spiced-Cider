package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.renderer.FogRenderer$BlindnessFogFunction")
public class BlindnessFogSoftMixin {

    @ModifyExpressionValue(method = "setupFog", at = @At(value = "CONSTANT", args = "floatValue=5.0"))
    private float spicedcider$fartherBlindnessFog(float distance) {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.softerBlindnessAndDarkness, false) ? 12.0F : distance;
    }
}
