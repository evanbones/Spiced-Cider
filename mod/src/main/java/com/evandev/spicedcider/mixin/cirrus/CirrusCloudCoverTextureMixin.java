package com.evandev.spicedcider.mixin.cirrus;

import com.evandev.spicedcider.weather.client.CloudCoverClient;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@IfModLoaded("cirrus")
@Mixin(targets = "com.jvn.cirrus.client.CirrusCloudRenderer", remap = false)
public class CirrusCloudCoverTextureMixin {

    @ModifyArg(method = "cloudTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/AbstractTexture;"))
    private ResourceLocation spicedcider$cloudCoverTexture(ResourceLocation original) {
        return CloudCoverClient.texture(original);
    }

    @ModifyArg(method = "renderSunMask", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"), index = 1)
    private ResourceLocation spicedcider$cloudCoverSunMask(ResourceLocation original) {
        return CloudCoverClient.texture(original);
    }
}
