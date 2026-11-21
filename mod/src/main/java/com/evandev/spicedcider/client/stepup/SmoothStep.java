package com.evandev.spicedcider.client.stepup;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.Util;

public class SmoothStep {
    private static final double TIME_CONSTANT = 0.06;
    private static final double MAX_GAP = 2.0;
    private static final double MAX_DROP = 1.6;
    private static final double MAX_ACTIVE_SECONDS = 1.5;
    private static final double FADE_SECONDS = 0.05;
    private static final double MAX_FRAME_SECONDS = 0.25;

    private static boolean stepped;
    private static boolean dropped;
    private static boolean active;
    private static boolean fading;
    private static double fade;
    private static double activeSeconds;
    private static double dropOrigin = Double.NaN;
    private static double cameraY;
    private static double lastY;
    private static long lastFrame;

    private static boolean enabled() {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.smoothStepUp, false);
    }

    public static void onMove(boolean grounded, boolean groundedAfter, double rise, double requestedY) {
        if (!enabled()) return;
        if (grounded && rise > 0.01 && rise < MAX_GAP) {
            stepped = true;
        } else if (grounded && !groundedAfter && requestedY <= 0.0) {
            dropped = true;
        }
    }

    public static double adjust(double y, boolean settled) {
        long now = Util.getNanos();
        double dt = Math.min(Math.max((now - lastFrame) / 1.0E9, 0.0), MAX_FRAME_SECONDS);
        lastFrame = now;
        if (stepped) {
            stepped = false;
            begin(Double.NaN);
        }
        if (dropped) {
            dropped = false;
            begin(active ? cameraY : lastY);
        }
        lastY = y;
        if (!active || Math.abs(y - cameraY) > MAX_GAP) {
            reset(y);
            return y;
        }
        activeSeconds += dt;
        if (activeSeconds > MAX_ACTIVE_SECONDS || (!Double.isNaN(dropOrigin) && dropOrigin - y > MAX_DROP)) {
            fading = true;
        }
        if (fading) {
            fade *= Math.exp(-dt / FADE_SECONDS);
        }
        double tau = TIME_CONSTANT * fade;
        cameraY = tau < 1.0E-4 ? y : cameraY + (y - cameraY) * (1.0 - Math.exp(-dt / tau));
        if (Math.abs(y - cameraY) < 1.0E-3 && (settled || fading)) {
            reset(y);
        }
        return cameraY;
    }

    private static void begin(double origin) {
        active = true;
        fading = false;
        fade = 1.0;
        activeSeconds = 0.0;
        dropOrigin = origin;
    }

    public static void reset(double y) {
        stepped = false;
        dropped = false;
        active = false;
        fading = false;
        dropOrigin = Double.NaN;
        cameraY = y;
        lastY = y;
    }
}
