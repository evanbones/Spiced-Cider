package com.evandev.spicedcider.mixin.sodium;

import com.evandev.spicedcider.compat.voxy.EdgeFogShaders;
import com.evandev.spicedcider.compat.voxy.VoxyFog;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat4v;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.DefaultShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("sodium")
@IfModLoaded("roxy")
@Mixin(value = DefaultShaderInterface.class, remap = false)
public class EdgeFogShaderInterfaceMixin {

    @Unique
    private GlUniformFloat4v spicedcider$edgeFog;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$bindEdgeFog(ShaderBindingContext context, ChunkShaderOptions options, CallbackInfo ci) {
        this.spicedcider$edgeFog = context.bindUniformOptional(EdgeFogShaders.TERRAIN_UNIFORM, GlUniformFloat4v::new);
    }

    @Inject(method = "setupState", at = @At("TAIL"))
    private void spicedcider$uploadEdgeFog(CallbackInfo ci) {
        if (this.spicedcider$edgeFog != null) this.spicedcider$edgeFog.set(VoxyFog.edgeStretch());
    }
}
