package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.VoxyBackgroundFog;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("roxy")
@IfModLoaded("modulation")
@Mixin(targets = "me.cortex.voxy.client.core.gl.shader.ShaderLoader$ShaderLoadingParser", remap = false)
public class VoxyBackgroundFogShaderMixin {

    @ModifyReturnValue(method = "loadShaderAsset", at = @At("RETURN"))
    private static String spicedcider$backgroundFog(String source) {
        return VoxyBackgroundFog.patchShader(source);
    }
}
