package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.compat.vista.VistaFeeds;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("vista")
@Mixin(value = FogRenderer.class, priority = 1500)
public class VistaMirrorFogMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void spicedcider$thickMirrorFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance,
                                                   boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        VistaFeeds.applyMirrorFog(fogMode);
    }
}
