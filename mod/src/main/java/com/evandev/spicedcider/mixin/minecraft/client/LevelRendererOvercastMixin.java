package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.weather.VisualWeather;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public class LevelRendererOvercastMixin {

    @WrapMethod(method = "renderSky")
    private void spicedcider$overcastSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera,
                                         boolean isFoggy, Runnable skyFogSetup, Operation<Void> original) {
        VisualWeather.enter();
        try {
            original.call(frustumMatrix, projectionMatrix, partialTick, camera, isFoggy, skyFogSetup);
        } finally {
            VisualWeather.exit();
        }
    }

    @WrapMethod(method = "renderClouds")
    private void spicedcider$overcastClouds(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick,
                                            double cameraX, double cameraY, double cameraZ, Operation<Void> original) {
        VisualWeather.enter();
        try {
            original.call(poseStack, frustumMatrix, projectionMatrix, partialTick, cameraX, cameraY, cameraZ);
        } finally {
            VisualWeather.exit();
        }
    }
}
