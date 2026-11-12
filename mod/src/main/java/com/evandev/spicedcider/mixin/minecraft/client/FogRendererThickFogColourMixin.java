package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.events.ThickBiomeFog;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FogRenderer.class)
public class FogRendererThickFogColourMixin {

    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;

    @WrapMethod(method = "setupColor")
    private static void spicedcider$dimThickFog(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks,
                                                float darkenWorldAmount, Operation<Void> original) {
        original.call(camera, partialTick, level, renderDistanceChunks, darkenWorldAmount);
        float scale = ThickBiomeFog.colourScale();
        if (scale >= 1.0F) return;
        fogRed *= scale;
        fogGreen *= scale;
        fogBlue *= scale;
        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
    }
}
