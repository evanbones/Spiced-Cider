package com.evandev.spicedcider.mixin.picturemode;

import com.evandev.spicedcider.compat.picturemode.PictureModeClouds;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import me.cortex.voxy.client.core.VoxyRenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("picturemode")
@IfModLoaded("voxy")
@Mixin(value = VoxyRenderSystem.class, remap = false)
public class VoxyPictureModeMixin {

    @Inject(method = "renderOpaque", at = @At("HEAD"), cancellable = true)
    private void spicedcider$hideLodsInPictureMode(CallbackInfo ci) {
        if (PictureModeClouds.active()) {
            ci.cancel();
        }
    }
}
