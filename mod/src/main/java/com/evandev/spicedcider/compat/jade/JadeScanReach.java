package com.evandev.spicedcider.compat.jade;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

public class JadeScanReach {
    private static final double FALLBACK_DISTANCE = 64.0;
    private static boolean resolved;
    private static Object scanManager;
    private static Method scanDistance;

    public static double scanDistance(Minecraft minecraft) {
        if (!resolved) {
            resolved = true;
            if (ModList.get().isLoaded("fieldguide")) {
                try {
                    Class<?> manager = Class.forName("com.evandev.fieldguide.client.scan.manager.FieldGuideScanManager");
                    scanManager = manager.getMethod("getInstance").invoke(null);
                    scanDistance = manager.getMethod("getScanDistance", Minecraft.class);
                } catch (ReflectiveOperationException | LinkageError e) {
                    scanManager = null;
                    scanDistance = null;
                }
            }
        }
        if (scanDistance != null) {
            try {
                double distance = ((Number) scanDistance.invoke(scanManager, minecraft)).doubleValue();
                if (distance > 0) return distance;
            } catch (ReflectiveOperationException e) {
                scanDistance = null;
            }
        }
        return FALLBACK_DISTANCE;
    }
}
