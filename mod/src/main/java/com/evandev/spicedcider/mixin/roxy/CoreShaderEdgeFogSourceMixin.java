package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.EdgeFogShaders;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Program;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.InputStream;
import java.nio.charset.Charset;

@IfModLoaded("roxy")
@IfModLoaded("sodium")
@Mixin(Program.class)
public class CoreShaderEdgeFogSourceMixin {

    @WrapOperation(method = "compileShaderInternal",
            at = @At(value = "INVOKE", target = "Lorg/apache/commons/io/IOUtils;toString(Ljava/io/InputStream;Ljava/nio/charset/Charset;)Ljava/lang/String;", remap = false))
    private static String spicedcider$edgeFog(InputStream input, Charset charset, Operation<String> original,
                                              @Local(argsOnly = true) Program.Type type, @Local(argsOnly = true, ordinal = 0) String name) {
        String source = original.call(input, charset);
        if (type != Program.Type.VERTEX) return source;
        return EdgeFogShaders.patchCore(name, source);
    }
}
