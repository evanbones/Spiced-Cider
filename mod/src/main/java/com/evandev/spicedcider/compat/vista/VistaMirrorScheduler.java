package com.evandev.spicedcider.compat.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.vista.accessor.AdaptiveUpdateSchedulerAccessor;
import net.mehvahdjukaar.moonlight.api.util.math.Vec2i;
import net.mehvahdjukaar.vista.client.AdaptiveUpdateScheduler;
import net.mehvahdjukaar.vista.client.textures.perspective.MirrorReflectionTexture;
import net.mehvahdjukaar.vista.client.textures.perspective.MirrorTextureManager;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public final class VistaMirrorScheduler {
    private static final double IMPORTANCE_FACTOR = 150.0;

    private static final Map<MirrorReflectionTexture, Entry> ENTRIES = new WeakHashMap<>();

    private static AdaptiveUpdateScheduler<ResourceLocation> scheduler;
    private static double builtBaseFps;
    private static double builtMinFps;
    private static double builtBudgetMs;

    private static boolean granted;
    private static int renderDepth;
    private static long renderStartNanos;

    private static AdaptiveUpdateScheduler<ResourceLocation> scheduler() {
        double baseFps = SpicedCiderConfig.CLIENT.vistaMirrorUpdateFps.get();
        double minFps = Math.min(SpicedCiderConfig.CLIENT.vistaMirrorMinUpdateFps.get(), baseFps);
        double budgetMs = SpicedCiderConfig.CLIENT.vistaMirrorThrottleBudgetMs.get();

        if (scheduler == null || baseFps != builtBaseFps || minFps != builtMinFps || budgetMs != builtBudgetMs) {
            scheduler = AdaptiveUpdateScheduler.builder()
                    .baseFps(baseFps)
                    .minFps(minFps)
                    .targetBudgetMs(budgetMs)
                    .evictAfterTicks(20 * 5)
                    .guardTargetFps(60)
                    .build();
            builtBaseFps = baseFps;
            builtMinFps = minFps;
            builtBudgetMs = budgetMs;
            ENTRIES.clear();
        }
        return scheduler;
    }

    public static void onEndOfFrame() {
        scheduler().onEndOfFrame();
        renderDepth = 0;
    }

    public static boolean shouldRender(MirrorReflectionTexture text, MirrorBlockEntity mirror, Vec3 eye) {
        granted = false;
        scheduler().runIfShouldUpdate(text.getTextureLocation(), () -> granted = true);
        if (!granted) return false;
        if (!text.hasRendered()) return true;

        Entry e = ENTRIES.computeIfAbsent(text, t -> new Entry());
        double scale = importanceScale(mirror, eye, text);

        double idleFps = Math.min(SpicedCiderConfig.CLIENT.vistaMirrorIdleUpdateFps.get(), builtBaseFps);
        if (idleFps < builtBaseFps
                && e.lastRenderedEye != null && e.lastRenderedEye.distanceToSqr(eye) < 1.0E-8) {
            scale *= idleFps / builtBaseFps;
        }

        e.credit += scale;
        if (e.credit < 1.0) return false;
        e.credit -= 1.0;
        e.lastRenderedEye = eye;
        return true;
    }

    public static void beginRender() {
        if (renderDepth++ == 0) {
            renderStartNanos = System.nanoTime();
        }
    }

    public static void endRender() {
        renderDepth = Math.max(0, renderDepth - 1);
        if (renderDepth != 0) return;
        var acc = (AdaptiveUpdateSchedulerAccessor) (Object) scheduler();
        acc.spicedcider$setAccumulatedUpdateNanos(
                acc.spicedcider$getAccumulatedUpdateNanos() + (System.nanoTime() - renderStartNanos));
    }

    private static double importanceScale(MirrorBlockEntity mirror, Vec3 eye, MirrorReflectionTexture text) {
        Vec2i connected = mirror.getConnectedCount();
        double area = Math.max(1, connected.x()) * (double) Math.max(1, connected.y());
        double distSq = Math.max(1.0, eye.distanceToSqr(Vec3.atCenterOf(mirror.getBlockPos())));
        double scale = IMPORTANCE_FACTOR * area / distSq;

        int lod = MirrorTextureManager.distanceLod(eye, mirror.getBlockPos());
        if (lod > 0) {
            scale *= 1 << (2 * lod);
        }

        int depth = text.getRecursionDepth();
        if (depth > 0) {
            scale /= 1 << Math.min(depth, 3);
        }
        return Math.min(1.0, scale);
    }

    private static final class Entry {
        double credit;
        Vec3 lastRenderedEye;
    }
}
