package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererCloudFarPlaneMixin {

    @ModifyArg(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V"),
            index = 2
    )
    private Matrix4f spicedcider$widenCloudFarPlane(Matrix4f projectionMatrix) {
        if (!SpicedCiderConfig.CLIENT.cloudFarPlaneFix.get()) {
            return projectionMatrix;
        }
        float cloudFarPlane = SpicedCiderConfig.CLIENT.cloudFarPlaneDistance.get();
        float a = projectionMatrix.m22();
        float b = projectionMatrix.m32();
        float near = b / (a - 1f);
        if (!(near > 0f) || !Float.isFinite(near)) {
            return projectionMatrix;
        }
        Matrix4f widened = new Matrix4f(projectionMatrix);
        widened.m22((cloudFarPlane + near) / (near - cloudFarPlane));
        widened.m32(2f * cloudFarPlane * near / (near - cloudFarPlane));
        return widened;
    }
}
