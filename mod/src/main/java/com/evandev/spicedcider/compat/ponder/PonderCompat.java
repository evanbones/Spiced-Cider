package com.evandev.spicedcider.compat.ponder;

import net.neoforged.fml.ModList;

public final class PonderCompat {

    public static final String MOD_ID = "ponder";

    public static void register() {
        if (ModList.get().isLoaded(MOD_ID)) {
            PonderBridge.register();
        }
    }
}
