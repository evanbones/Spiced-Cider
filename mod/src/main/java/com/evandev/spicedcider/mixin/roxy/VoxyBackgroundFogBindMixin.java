package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.VoxyBackgroundFog;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("roxy")
@IfModLoaded("modulation")
@Mixin(targets = "me.cortex.voxy.client.core.NormalRenderPipeline", remap = false)
public class VoxyBackgroundFogBindMixin {

    @Inject(method = "finish", at = @At(value = "INVOKE", target = "Lme/cortex/voxy/client/core/rendering/post/FullscreenBlit;bind()V", shift = At.Shift.AFTER))
    private void spicedcider$bindBackground(CallbackInfo ci) {
        VoxyBackgroundFog.bind();
    }
}
