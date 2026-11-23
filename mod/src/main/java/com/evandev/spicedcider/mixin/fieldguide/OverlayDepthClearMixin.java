package com.evandev.spicedcider.mixin.fieldguide;

import com.evandev.spicedcider.client.render.OverlayDepthGuard;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("fieldguide")
@Mixin(targets = {
        "com.evandev.fieldguide.client.render.DiscoveryOverlayRenderer",
        "com.evandev.fieldguide.client.render.ScanOverlayRenderer"
}, remap = false)
public class OverlayDepthClearMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V"))
    private static void spicedcider$saveDepthBeforeClear(int mask, boolean checkError, Operation<Void> original) {
        OverlayDepthGuard.saveOnce();
        original.call(mask, checkError);
    }
}
