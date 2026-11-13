package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.roxy.fog.RoxyVoxyFogPatch;
import com.evandev.spicedcider.roxy.loader.RoxyBundle;
import com.mojang.blaze3d.shaders.FogShape;

public final class BundledRoxy {
    private static final boolean PRESENT = present();

    public static boolean active() {
        return PRESENT && RoxyBundle.active();
    }

    public static void overrideTerrainFog(float start, float end, FogShape shape) {
        if (active()) RoxyVoxyFogPatch.overrideTerrainFog(start, end, shape.ordinal());
    }

    private static boolean present() {
        try {
            Class.forName("com.evandev.spicedcider.roxy.loader.RoxyBundle", false, BundledRoxy.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }
}
