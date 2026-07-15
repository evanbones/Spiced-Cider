package com.evandev.spicedcider.compat.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.mehvahdjukaar.moonlight.api.util.math.Vec2i;
import net.mehvahdjukaar.vista.client.textures.MirrorReflectionTexture;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlockEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public final class VistaMirrorScheduler {
    private static final int MAX_FIRST_RENDERS_PER_FRAME = 2;
    private static final double IMPORTANCE_FACTOR = 150.0;

    private static final Map<MirrorReflectionTexture, Entry> ENTRIES = new WeakHashMap<>();

    private static int firstRendersLeftThisFrame = MAX_FIRST_RENDERS_PER_FRAME;
    private static double budgetUsedMsThisFrame = 0;

    public static void onFrameStart() {
        firstRendersLeftThisFrame = MAX_FIRST_RENDERS_PER_FRAME;
        budgetUsedMsThisFrame = 0;
    }

    public static void maybeRender(MirrorReflectionTexture text, MirrorBlockEntity mirror, Vec3 eye) {
        if (!text.hasRendered()) {
            if (firstRendersLeftThisFrame <= 0) return;
            firstRendersLeftThisFrame--;
            render(text, mirror, eye);
            return;
        }

        Entry e = ENTRIES.computeIfAbsent(text, t -> new Entry());
        long now = System.nanoTime();
        if (now < e.nextEligibleNanos) return;

        double baseFps = SpicedCiderConfig.CLIENT.vistaMirrorUpdateFps.get();
        double minFps = SpicedCiderConfig.CLIENT.vistaMirrorMinUpdateFps.get();
        double idleFps = SpicedCiderConfig.CLIENT.vistaMirrorIdleUpdateFps.get();
        double budgetMs = SpicedCiderConfig.CLIENT.vistaMirrorThrottleBudgetMs.get();

        boolean overBudget = budgetUsedMsThisFrame >= budgetMs;
        render(text, mirror, eye);
        double scale = importanceScale(mirror, eye, text);

        boolean idle = e.lastRenderedEye != null && e.lastRenderedEye.distanceToSqr(eye) < 1.0E-8;
        if (idle && idleFps < baseFps) {
            scale *= idleFps / baseFps;
        }
        if (overBudget) {
            scale = Math.min(scale, minFps / baseFps);
        }

        e.lastRenderedEye = eye;
        if (scale >= 1.0 && !overBudget) {
            e.nextEligibleNanos = now;
        } else {
            double rateHz = Mth.clamp(baseFps * scale, minFps, baseFps);
            e.nextEligibleNanos = now + (long) (1.0e9 / Math.max(rateHz, 0.01));
        }
    }

    private static void render(MirrorReflectionTexture text, MirrorBlockEntity mirror, Vec3 eye) {
        long start = System.nanoTime();
        text.renderReflection(mirror, eye);
        budgetUsedMsThisFrame += (System.nanoTime() - start) / 1.0e6;
    }

    private static double importanceScale(MirrorBlockEntity mirror, Vec3 eye, MirrorReflectionTexture text) {
        Vec2i connected = mirror.getConnectedCount();
        double area = Math.max(1, connected.x()) * (double) Math.max(1, connected.y());
        double distSq = Math.max(1.0, eye.distanceToSqr(Vec3.atCenterOf(mirror.getBlockPos())));
        double scale = Math.min(1.0, IMPORTANCE_FACTOR * area / distSq);

        int depth = text.getRecursionDepth();
        if (depth > 0) {
            scale /= (1 << Math.min(depth, 3));
        }
        return scale;
    }

    private static final class Entry {
        long nextEligibleNanos = 0L;
        Vec3 lastRenderedEye;
    }
}
