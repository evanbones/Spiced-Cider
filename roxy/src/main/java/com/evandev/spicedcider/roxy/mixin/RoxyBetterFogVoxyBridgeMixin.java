package com.evandev.spicedcider.roxy.mixin;

import com.evandev.spicedcider.roxy.fog.RoxyVoxyFogPatch;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("betterfog")
@Mixin(targets = "net.cloud.betterfog.client.compat.VoxyBridge", remap = false)
public abstract class RoxyBetterFogVoxyBridgeMixin {
    @Inject(method = "lodDistanceBlocks()I", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void roxy$readFloatVoxyDistance(CallbackInfoReturnable<Integer> cir) {
        float distance = RoxyVoxyFogPatch.lodDistanceBlocks();
        if (Float.isFinite(distance) && distance > 0.0F) {
            cir.setReturnValue(Math.round(distance));
        }
    }
}
