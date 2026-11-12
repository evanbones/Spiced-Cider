package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.EdgeFogShaders;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.rendering.section.backend.mdic.MDICSectionRenderer", remap = false)
public class VoxyTranslucentEdgeFogMixin {

    @Inject(method = "renderTranslucent", at = @At(value = "INVOKE", target = "Lme/cortex/voxy/client/core/gl/shader/Shader;bind()V", shift = At.Shift.AFTER))
    private void spicedcider$bindEdgeFog(CallbackInfo ci) {
        EdgeFogShaders.bindLod();
    }
}
