package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.VoxyFog;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("roxy")
@Mixin(FogRenderer.class)
public class FogRendererVoxyFogMixin {

    @Inject(method = "setupFog", at = @At("HEAD"))
    private static void spicedcider$prepareVoxyFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance,
                                                   boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        VoxyFog.prepare(camera, fogMode, farPlaneDistance, shouldCreateFog, partialTick);
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void spicedcider$applyVoxyFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance,
                                                 boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        VoxyFog.finish(fogMode);
    }
}
