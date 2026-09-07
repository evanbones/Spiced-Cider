package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ClutterNoMoreReloadState;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("clutternomore")
@Mixin(value = AssetGenerator.class, remap = false)
public abstract class AssetGeneratorWrappedTexturesMixin {

    @WrapOperation(
            method = "getTextures",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/gson/JsonObject;getAsJsonObject(Ljava/lang/String;)Lcom/google/gson/JsonObject;"
            )
    )
    private static JsonObject spicedcider$unwrapWrappedModelTextures(JsonObject parentModelJson, String memberName, Operation<JsonObject> original) {
        JsonObject textures = original.call(parentModelJson, memberName);
        if (textures == null && parentModelJson.has("wrapped_model")) {
            JsonObject wrapped = parentModelJson.getAsJsonObject("wrapped_model");
            if (wrapped != null) textures = wrapped.getAsJsonObject(memberName);
        }
        return textures;
    }

    @Inject(method = "getTextures", at = @At("RETURN"))
    private static void spicedcider$trackMissingTextures(ResourceManager manager, ResourceLocation parent, CallbackInfoReturnable<JsonObject> cir) {
        if (cir.getReturnValue() == null) {
            ClutterNoMoreReloadState.markMissedTextures();
        }
    }
}
