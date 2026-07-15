package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(value = VistaLevelRenderer.class, remap = false)
public class VistaOffAxisFrustumCacheMixin {

    @Unique
    private static final Map<SectionOcclusionGraph, Vec3> LAST_OFF_AXIS_EYE = new WeakHashMap<>();

    @ModifyVariable(
            method = "onSetupRenderer",
            at = @At("STORE"),
            name = "hasOffAxis"
    )
    private static boolean spicedcider$onlyRebuildWhenOffAxisEyeMoved(
            boolean hasOffAxis, LevelRenderer lr, Camera camera, Frustum frustum,
            boolean hasCapturedFrustum, boolean isSpectator,
            @Local(name = "graph") SectionOcclusionGraph graph) {
        if (!hasOffAxis || !SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            return hasOffAxis;
        }

        Vec3 eye = camera.getPosition();
        Vec3 lastEye = LAST_OFF_AXIS_EYE.get(graph);
        if (lastEye != null && lastEye.distanceToSqr(eye) < 1.0E-8) {
            return false;
        }
        LAST_OFF_AXIS_EYE.put(graph, eye);
        return true;
    }
}
