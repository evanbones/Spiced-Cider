package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.events.ThickBiomeFog;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererThickFogSkyMixin {

    @ModifyReturnValue(method = "doesMobEffectBlockSky", at = @At("RETURN"))
    private boolean spicedcider$thickFogHidesSky(boolean blocked) {
        return blocked || ThickBiomeFog.hidesSky();
    }
}
