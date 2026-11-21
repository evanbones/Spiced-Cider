package com.evandev.spicedcider.client.stepup;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.Util;

public class SmoothStep {
    private static final double TIME_CONSTANT = 0.06;
    private static final double MAX_RISE = 1.5;
    private static final double MAX_FRAME_SECONDS = 0.25;

    private static boolean stepped;
    private static boolean active;
    private static double cameraY;
    private static long lastFrame;

    public static void onMove(double rise) {
        if (rise > 0.01 && rise < MAX_RISE && SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.smoothStepUp, false)) {
            stepped = true;
        }
    }

    public static double adjust(double vanillaY) {
        long now = Util.getNanos();
        double dt = Math.min(Math.max((now - lastFrame) / 1.0E9, 0.0), MAX_FRAME_SECONDS);
        lastFrame = now;
        if (stepped) {
            stepped = false;
            active = true;
        }
        if (!active || vanillaY <= cameraY || vanillaY - cameraY > MAX_RISE) {
            active = false;
            cameraY = vanillaY;
            return vanillaY;
        }
        cameraY += (vanillaY - cameraY) * (1.0 - Math.exp(-dt / TIME_CONSTANT));
        if (vanillaY - cameraY < 1.0E-3) {
            active = false;
            cameraY = vanillaY;
        }
        return cameraY;
    }

    public static void reset(double y) {
        stepped = false;
        active = false;
        cameraY = y;
    }
}
