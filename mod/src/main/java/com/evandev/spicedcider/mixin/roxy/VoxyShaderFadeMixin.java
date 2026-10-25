package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.VoxyFadeIn;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.gl.shader.ShaderLoader$ShaderLoadingParser", remap = false)
public class VoxyShaderFadeMixin {

    @ModifyReturnValue(method = "loadShaderAsset", at = @At("RETURN"))
    private static String spicedcider$patchFadeIn(String source) {
        return VoxyFadeIn.patchShader(source);
    }
}
