package com.evandev.spicedcider.mixin.modulation;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("modulation")
@IfModLoaded("vista")
@Mixin(targets = "com.evandev.modulation.client.ImprovedFog", remap = false)
public abstract class ImprovedFogVistaFeedMixin {

    @Unique
    private static final int spicedcider$MAX_FEED_TARGETS = 8;

    @Unique
    private static final Int2ObjectMap<RenderTarget[]> spicedcider$feedTargets = new Int2ObjectOpenHashMap<>();

    @Unique
    private static RenderTarget[] spicedcider$mainTargets;

    @Unique
    private static int spicedcider$feedKey;

    @Shadow
    private static RenderTarget skyTarget;

    @Shadow
    private static RenderTarget cloudsTarget;

    @Inject(method = "captureBackground", at = @At("HEAD"))
    private static void spicedcider$useFeedTargets(CallbackInfo ci) {
        spicedcider$restoreMainTargets();
        if (!VistaLevelRenderer.isRenderingLiveFeed() || !SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            return;
        }

        spicedcider$feedKey = GlStateManager.Viewport.width() << 16 | GlStateManager.Viewport.height();
        spicedcider$mainTargets = new RenderTarget[]{skyTarget, cloudsTarget};
        RenderTarget[] feed = spicedcider$feedTargets.get(spicedcider$feedKey);
        skyTarget = feed != null ? feed[0] : null;
        cloudsTarget = feed != null ? feed[1] : null;
    }

    @Inject(method = "endFrame", at = @At("TAIL"))
    private static void spicedcider$restoreAfterFeed(CallbackInfo ci) {
        spicedcider$restoreMainTargets();
    }

    @Unique
    private static void spicedcider$restoreMainTargets() {
        RenderTarget[] main = spicedcider$mainTargets;
        if (main == null) return;
        spicedcider$mainTargets = null;

        if (skyTarget != null && cloudsTarget != null) {
            RenderTarget[] previous = spicedcider$feedTargets.get(spicedcider$feedKey);
            if (previous == null && spicedcider$feedTargets.size() >= spicedcider$MAX_FEED_TARGETS) {
                spicedcider$destroyFeedTargets();
            }
            spicedcider$feedTargets.put(spicedcider$feedKey, new RenderTarget[]{skyTarget, cloudsTarget});
        }
        skyTarget = main[0];
        cloudsTarget = main[1];
    }

    @Inject(method = "release", at = @At("HEAD"))
    private static void spicedcider$releaseFeedTargets(CallbackInfo ci) {
        if (spicedcider$mainTargets == null) {
            spicedcider$destroyFeedTargets();
        }
    }

    @Unique
    private static void spicedcider$destroyFeedTargets() {
        for (RenderTarget[] pair : spicedcider$feedTargets.values()) {
            pair[0].destroyBuffers();
            pair[1].destroyBuffers();
        }
        spicedcider$feedTargets.clear();
    }
}
