package com.evandev.spicedcider.mixin.bettercloudshadows;

import com.evandev.better_cloud_shadows.client.BlockLightTexture;
import com.evandev.better_cloud_shadows.client.CloudCoverageTexture;
import com.evandev.better_cloud_shadows.clouds.CloudField;
import com.evandev.better_cloud_shadows.client.CloudShadowRenderer;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("better_cloud_shadows")
@IfModLoaded("vista")
@Mixin(value = CloudShadowRenderer.class, remap = false)
public abstract class CloudShadowVistaFeedMixin {

    @Unique
    private static final int spicedcider$MAX_FEED_TARGETS = 8;

    @Unique
    private static final Int2ObjectMap<RenderTarget> spicedcider$feedDepthCopies = new Int2ObjectOpenHashMap<>();

    @Unique
    private static final Int2ObjectMap<Boolean> spicedcider$feedDepthStencils = new Int2ObjectOpenHashMap<>();

    @Shadow
    private static RenderTarget depthCopy;

    @Shadow
    private static boolean depthCopyStencil;

    @Unique
    private static boolean spicedcider$inFeed() {
        return VistaLevelRenderer.isRenderingLiveFeed() && SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get();
    }

    @WrapMethod(method = "render")
    private static void spicedcider$separateFeedDepthCopy(Matrix4f modelViewMatrix, Matrix4f projectionMatrix,
                                                          Camera cameraView, float partialTick, Operation<Void> original) {
        if (!spicedcider$inFeed()) {
            original.call(modelViewMatrix, projectionMatrix, cameraView, partialTick);
            return;
        }

        RenderTarget target = Minecraft.getInstance().getMainRenderTarget();
        int key = target.width << 16 | target.height;
        RenderTarget mainCopy = depthCopy;
        boolean mainStencil = depthCopyStencil;
        depthCopy = spicedcider$feedDepthCopies.get(key);
        depthCopyStencil = spicedcider$feedDepthStencils.getOrDefault(key, Boolean.FALSE);
        try {
            original.call(modelViewMatrix, projectionMatrix, cameraView, partialTick);
        } finally {
            RenderTarget feedCopy = depthCopy;
            boolean feedStencil = depthCopyStencil;
            depthCopy = mainCopy;
            depthCopyStencil = mainStencil;
            if (feedCopy != null) {
                if (spicedcider$feedDepthCopies.get(key) != feedCopy
                        && spicedcider$feedDepthCopies.size() >= spicedcider$MAX_FEED_TARGETS) {
                    spicedcider$destroyFeedCopies();
                }
                spicedcider$feedDepthCopies.put(key, feedCopy);
                spicedcider$feedDepthStencils.put(key, (Boolean) feedStencil);
            }
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/evandev/better_cloud_shadows/client/BlockLightTexture;update(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/Camera;Z)V")
    )
    private static void spicedcider$keepMainCameraLightWindow(BlockLightTexture texture, ClientLevel level, Camera camera,
                                                              boolean affectedByLights, Operation<Void> original) {
        if (!spicedcider$inFeed()) {
            original.call(texture, level, camera, affectedByLights);
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE",
                    target = "Lcom/evandev/better_cloud_shadows/client/CloudCoverageTexture;update(ILcom/evandev/better_cloud_shadows/clouds/CloudField$Layer;IIIF)Z")
    )
    private static boolean spicedcider$keepMainCameraCoverage(CloudCoverageTexture coverage, int layer, CloudField.Layer source,
                                                              int centerX, int centerZ, int dilate, float blur,
                                                              Operation<Boolean> original) {
        if (spicedcider$inFeed() && coverage.uploadedLayers() > layer) {
            return false;
        }
        return original.call(coverage, layer, source, centerX, centerZ, dilate, blur);
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private static void spicedcider$resetFeedCopies(CallbackInfo ci) {
        spicedcider$destroyFeedCopies();
    }

    @Unique
    private static void spicedcider$destroyFeedCopies() {
        for (RenderTarget copy : spicedcider$feedDepthCopies.values()) {
            copy.destroyBuffers();
        }
        spicedcider$feedDepthCopies.clear();
        spicedcider$feedDepthStencils.clear();
    }
}
