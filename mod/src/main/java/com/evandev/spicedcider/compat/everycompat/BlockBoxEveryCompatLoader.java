package com.evandev.spicedcider.compat.everycompat;

import net.mehvahdjukaar.every_compat.api.EveryCompatAPI;

public class BlockBoxEveryCompatLoader {
    public static void register(String modId) {
        EveryCompatAPI.registerModule(new BlockBoxEveryCompat(modId));
    }
}
