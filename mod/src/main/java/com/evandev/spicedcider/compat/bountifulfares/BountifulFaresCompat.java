package com.evandev.spicedcider.compat.bountifulfares;

import net.neoforged.fml.ModList;

public final class BountifulFaresCompat {

    public static final String MOD_ID = "bountifulfares";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
}
