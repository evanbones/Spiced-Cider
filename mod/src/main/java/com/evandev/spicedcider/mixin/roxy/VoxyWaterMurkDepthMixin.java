package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.LodWaterMurk;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import me.cortex.voxy.client.core.AbstractRenderPipeline;
import me.cortex.voxy.client.core.NormalRenderPipeline;
import me.cortex.voxy.client.core.gl.GlTexture;
import me.cortex.voxy.client.core.rendering.Viewport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("roxy")
@Mixin(value = NormalRenderPipeline.class, remap = false)
public class VoxyWaterMurkDepthMixin {

    @Inject(method = "setupAndBindTranslucent", at = @At("HEAD"))
    private void spicedcider$copyLodDepth(Viewport<?> viewport, CallbackInfo ci) {
        GlTexture depth = ((AbstractRenderPipeline) (Object) this).fb.getDepthTex();
        if (depth == null) return;
        LodWaterMurk.beforeTranslucent(depth.id, depth.getWidth(), depth.getHeight(), depth.getFormat());
    }
}
