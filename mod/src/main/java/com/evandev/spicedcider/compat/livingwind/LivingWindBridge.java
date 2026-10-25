package com.evandev.spicedcider.compat.livingwind;

import com.macatmovie.livingwind.api.ClientWindApi;

public final class LivingWindBridge {

    public static boolean hasWind() {
        return ClientWindApi.hasWindState();
    }

    public static float strength() {
        return ClientWindApi.globalStrength();
    }

    public static float windX() {
        return ClientWindApi.globalWindX();
    }

    public static float windZ() {
        return ClientWindApi.globalWindZ();
    }
}
