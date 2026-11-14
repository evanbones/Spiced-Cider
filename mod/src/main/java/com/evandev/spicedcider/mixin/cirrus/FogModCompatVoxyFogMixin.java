package com.evandev.spicedcider.mixin.cirrus;

import com.evandev.spicedcider.compat.voxy.VoxyFog;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("cirrus")
@IfModLoaded("roxy")
@Mixin(targets = "com.jvn.cirrus.client.compat.fog.FogModCompat", remap = false)
public class FogModCompatVoxyFogMixin {

    @Inject(method = "controlsFogDistance", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$voxyFogControlsDistance(CallbackInfoReturnable<Boolean> cir) {
        if (VoxyFog.appliedTerrainFog()) cir.setReturnValue(true);
    }
}
