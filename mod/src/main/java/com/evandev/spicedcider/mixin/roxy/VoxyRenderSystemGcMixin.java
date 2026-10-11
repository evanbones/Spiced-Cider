package com.evandev.spicedcider.mixin.roxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.VoxyRenderSystem", remap = false)
public class VoxyRenderSystemGcMixin {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void spicedcider$skipExplicitGc(Operation<Void> original) {
    }
}
