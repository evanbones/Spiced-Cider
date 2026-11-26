package com.evandev.spicedcider.mixin.sodium;

import com.evandev.spicedcider.compat.sodium.WaterDepthFog;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("sodium")
@Mixin(value = ShaderLoader.class, remap = false)
public class WaterDepthFogShaderLoaderMixin {

    @WrapMethod(method = "getShaderSource")
    private static String spicedcider$waterDepthFog(ResourceLocation name, Operation<String> original) {
        String source = original.call(name);
        if (!name.getNamespace().equals("sodium")) return source;
        return switch (name.getPath()) {
            case "blocks/block_layer_opaque.vsh" -> WaterDepthFog.patchVertex(source);
            case "blocks/block_layer_opaque.fsh" -> WaterDepthFog.patchFragment(source);
            default -> source;
        };
    }
}
