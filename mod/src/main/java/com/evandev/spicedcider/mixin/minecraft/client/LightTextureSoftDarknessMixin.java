package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public class LightTextureSoftDarknessMixin {

    @ModifyReturnValue(method = "getDarknessGamma", at = @At("RETURN"))
    private float spicedcider$softerDarkness(float gamma) {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.softerBlindnessAndDarkness, false) ? gamma * 0.4F : gamma;
    }
}
