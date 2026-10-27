package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.nether.NetherAmbience;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightTexture.class)
public class LightTextureNetherTintMixin {

    @WrapOperation(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;adjustLightmapColors(Lnet/minecraft/client/multiplayer/ClientLevel;FFFFIILorg/joml/Vector3f;)V"))
    private void spicedcider$tintNetherAmbient(DimensionSpecialEffects effects, ClientLevel level, float partialTicks, float skyDarken, float blockLightRedFlicker, float skyLight, int blockLight, int skyLightLevel, Vector3f colors, Operation<Void> original) {
        original.call(effects, level, partialTicks, skyDarken, blockLightRedFlicker, skyLight, blockLight, skyLightLevel, colors);
        NetherAmbience.tintLightmap(colors, blockLight);
    }
}
