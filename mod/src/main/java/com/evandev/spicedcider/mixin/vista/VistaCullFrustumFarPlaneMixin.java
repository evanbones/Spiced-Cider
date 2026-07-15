package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.vista.accessor.GameRendererAccessor;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = VistaLevelRenderer.class, remap = false)
public class VistaCullFrustumFarPlaneMixin {

    @Redirect(
            method = "renderLevel(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/pipeline/RenderTarget;Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;prepareCullFrustum(Lnet/minecraft/world/phys/Vec3;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V")
    )
    private static void spicedcider$tightenCullFarPlane(LevelRenderer lr, Vec3 cameraPosition, Matrix4f frustumMatrix, Matrix4f projectionMatrix) {
        if (!SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            lr.prepareCullFrustum(cameraPosition, frustumMatrix, projectionMatrix);
            return;
        }
        float renderDistanceBlocks = ((GameRendererAccessor) Minecraft.getInstance().gameRenderer).spicedcider$getRenderDistance();
        float cullFar = Math.max(renderDistanceBlocks, 32f) * 1.15f + 32f;
        lr.prepareCullFrustum(cameraPosition, frustumMatrix, spicedcider$tightenFarPlane(projectionMatrix, cullFar));
    }

    @Unique
    private static Matrix4f spicedcider$tightenFarPlane(Matrix4f proj, float maxFar) {
        float a = proj.m22();
        float b = proj.m32();
        float near = b / (a - 1f);
        float far = b / (a + 1f);
        if (!(near > 0f) || !Float.isFinite(far) || far <= maxFar) return proj;

        Matrix4f tightened = new Matrix4f(proj);
        tightened.m22((maxFar + near) / (near - maxFar));
        tightened.m32(2f * maxFar * near / (near - maxFar));
        return tightened;
    }
}
