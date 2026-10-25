package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.weather.client.CirrusCloudShader;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Program.class)
public class ProgramCloudFadeMixin {

    @WrapOperation(method = "compileShaderInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;process(Ljava/lang/String;)Ljava/util/List;"))
    private static List<String> spicedcider$fadeCirrusClouds(GlslPreprocessor preprocessor, String source, Operation<List<String>> original,
                                                              @Local(argsOnly = true) Program.Type type, @Local(argsOnly = true, ordinal = 0) String name) {
        return original.call(preprocessor, type == Program.Type.FRAGMENT ? CirrusCloudShader.patch(name, source) : source);
    }
}
