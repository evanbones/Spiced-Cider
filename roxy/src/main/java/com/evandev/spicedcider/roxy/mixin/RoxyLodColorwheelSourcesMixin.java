package com.evandev.spicedcider.roxy.mixin;

import org.spongepowered.asm.mixin.injection.Coerce;
import com.evandev.spicedcider.roxy.shader.RoxyLodEntityShader;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("colorwheel")
@Mixin(targets = "dev.djefrey.colorwheel.compile.ClrwlProgramSources", remap = false)
public abstract class RoxyLodColorwheelSourcesMixin {
    @Inject(
            method = "getGbuffersSources(Ldev/djefrey/colorwheel/shaderpack/ClrwlProgramId;Ldev/djefrey/colorwheel/compile/ClrwlPrograms$OitMode;I)Ldev/djefrey/colorwheel/compile/ClrwlProgramSources$PatchedGbuffersSources;",
            at = @At("RETURN"), require = 0, cancellable = true
    )
    private void roxy$patchGbuffers(@Coerce Object programId, @Coerce Object oitMode, int ssboOffset,
                                    CallbackInfoReturnable<Object> callbackInfo) {
        callbackInfo.setReturnValue(RoxyLodEntityShader.patchColorwheelResult(callbackInfo.getReturnValue(), programId));
    }
}
