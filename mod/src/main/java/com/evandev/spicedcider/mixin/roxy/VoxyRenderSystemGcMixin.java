package com.evandev.spicedcider.mixin.roxy;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.VoxyRenderSystem", remap = false)
public class VoxyRenderSystemGcMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void spicedcider$skipExplicitGc() {
    }
}
