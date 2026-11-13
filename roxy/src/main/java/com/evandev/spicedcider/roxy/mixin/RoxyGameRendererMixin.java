package com.evandev.spicedcider.roxy.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GameRenderer.class)
public abstract class RoxyGameRendererMixin {
    @Shadow
    private double getFov(Camera camera, float partialTick, boolean useFovSetting) {
        throw new AssertionError();
    }

    @Unique
    public float roxy$getFov(Camera camera, float partialTick, boolean useFovSetting) {
        return (float) getFov(camera, partialTick, useFovSetting);
    }

    @Shadow
    public Matrix4f getProjectionMatrix(double fov) {
        throw new AssertionError();
    }

    @Unique
    public Matrix4f roxy$getProjectionMatrix(float fov) {
        return getProjectionMatrix((double) fov);
    }
}
