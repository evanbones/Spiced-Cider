package com.evandev.spicedcider.mixin.sodium;

import com.evandev.spicedcider.compat.voxy.EdgeFogShaders;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("sodium")
@IfModLoaded("roxy")
@Mixin(value = ShaderLoader.class, remap = false)
public class EdgeFogShaderLoaderMixin {

    @WrapMethod(method = "getShaderSource")
    private static String spicedcider$edgeFog(ResourceLocation name, Operation<String> original) {
        String source = original.call(name);
        if (!name.getNamespace().equals("sodium") || !name.getPath().equals("blocks/block_layer_opaque.vsh")) return source;
        return EdgeFogShaders.patchTerrain(source);
    }
}
