package com.evandev.spicedcider.mixin.sodium;

import com.evandev.spicedcider.compat.sodium.WaterDepthFog;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformFloat4v;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformInt;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.DefaultShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("sodium")
@Mixin(value = DefaultShaderInterface.class, remap = false)
public class WaterDepthFogShaderInterfaceMixin {

    @Unique
    private boolean spicedcider$translucent;
    @Unique
    private GlUniformFloat4v spicedcider$waterFog;
    @Unique
    private GlUniformInt spicedcider$sceneDepth;
    @Unique
    private final GlUniformFloat4v[] spicedcider$waterUv = new GlUniformFloat4v[WaterDepthFog.SPRITE_SLOTS];

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$bindWaterFog(ShaderBindingContext context, ChunkShaderOptions options, CallbackInfo ci) {
        this.spicedcider$translucent = options.pass().isTranslucent();
        this.spicedcider$waterFog = context.bindUniformOptional(WaterDepthFog.FOG_UNIFORM, GlUniformFloat4v::new);
        this.spicedcider$sceneDepth = context.bindUniformOptional(WaterDepthFog.DEPTH_UNIFORM, GlUniformInt::new);
        for (int i = 0; i < WaterDepthFog.SPRITE_SLOTS; i++) {
            this.spicedcider$waterUv[i] = context.bindUniformOptional(WaterDepthFog.UV_UNIFORM + "[" + i + "]", GlUniformFloat4v::new);
        }
    }

    @Inject(method = "setupState", at = @At("TAIL"))
    private void spicedcider$uploadWaterFog(CallbackInfo ci) {
        if (this.spicedcider$waterFog == null) return;
        boolean bound = this.spicedcider$translucent && WaterDepthFog.bind();
        this.spicedcider$waterFog.set(bound ? WaterDepthFog.fog() : WaterDepthFog.DISABLED);
        if (!bound) return;
        if (this.spicedcider$sceneDepth != null) this.spicedcider$sceneDepth.setInt(WaterDepthFog.DEPTH_UNIT);
        for (int i = 0; i < WaterDepthFog.SPRITE_SLOTS; i++) {
            if (this.spicedcider$waterUv[i] != null) this.spicedcider$waterUv[i].set(WaterDepthFog.rect(i));
        }
    }
}
