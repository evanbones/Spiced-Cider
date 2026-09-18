package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.vista.accessor.LevelRendererAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("vista")
@Mixin(value = VistaLevelRenderer.class, remap = false)
public class VistaSharedStateThrashMixin {

    @WrapOperation(
            method = "renderLevel(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/pipeline/RenderTarget;Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V")
    )
    private static void spicedcider$isolateTranslucencySort(
            LevelRenderer lr, DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera,
            GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix,
            Matrix4f projectionMatrix, Operation<Void> original) {
        if (!SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            original.call(lr, deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture,
                    frustumMatrix, projectionMatrix);
            return;
        }

        LevelRendererAccessor acc = (LevelRendererAccessor) lr;
        double oldX = acc.spicedcider$getXTransparentOld();
        double oldY = acc.spicedcider$getYTransparentOld();
        double oldZ = acc.spicedcider$getZTransparentOld();

        Vec3 eye = camera.getPosition();
        acc.spicedcider$setXTransparentOld(eye.x);
        acc.spicedcider$setYTransparentOld(eye.y);
        acc.spicedcider$setZTransparentOld(eye.z);
        try {
            original.call(lr, deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture,
                    frustumMatrix, projectionMatrix);
        } finally {
            acc.spicedcider$setXTransparentOld(oldX);
            acc.spicedcider$setYTransparentOld(oldY);
            acc.spicedcider$setZTransparentOld(oldZ);
        }
    }
}
