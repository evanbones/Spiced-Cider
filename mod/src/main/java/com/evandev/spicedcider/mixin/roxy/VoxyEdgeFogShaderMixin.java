package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.EdgeFogShaders;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.gl.shader.ShaderLoader$ShaderLoadingParser", remap = false)
public class VoxyEdgeFogShaderMixin {

    @ModifyReturnValue(method = "loadShaderAsset", at = @At("RETURN"))
    private static String spicedcider$edgeFog(String source) {
        return EdgeFogShaders.patchLod(source);
    }
}
