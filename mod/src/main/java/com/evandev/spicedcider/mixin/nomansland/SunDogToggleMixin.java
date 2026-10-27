package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.farcr.nomansland.client.renderer.SunDogRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("nomansland")
@Mixin(value = SunDogRenderer.class, remap = false)
public class SunDogToggleMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void spicedcider$toggle(PoseStack poseStack, Matrix4f projection, float partialTick, CallbackInfo ci) {
        if (!SpicedCiderConfig.CLIENT.sunDogs.get()) ci.cancel();
    }
}
