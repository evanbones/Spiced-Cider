package com.evandev.spicedcider.mixin.fieldguide;

import com.evandev.spicedcider.client.render.OverlayDepthGuard;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("fieldguide")
@Mixin(targets = "com.evandev.fieldguide.client.render.LevelOverlays", remap = false)
public class LevelOverlaysDepthMixin {

    @WrapMethod(method = "renderPending")
    private static void spicedcider$restoreDepth(Operation<Void> original) {
        try {
            original.call();
        } finally {
            OverlayDepthGuard.restore();
        }
    }
}
