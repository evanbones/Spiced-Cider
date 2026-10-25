package com.evandev.spicedcider.compat.modulation;

import com.evandev.modulation.client.ImprovedFog;

public final class ModulationFogBridge {

    public static boolean bindBackground() {
        if (!ImprovedFog.isActive()) return false;
        ImprovedFog.bindBackgroundTextures();
        return true;
    }
}
