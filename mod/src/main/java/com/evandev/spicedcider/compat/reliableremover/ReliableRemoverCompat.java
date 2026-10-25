package com.evandev.spicedcider.compat.reliableremover;

import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

public final class ReliableRemoverCompat {

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded("reliable_remover");
        }
        return loaded;
    }

    public static boolean isRemoved(Item item) {
        return isLoaded() && ReliableRemoverBridge.isRemoved(item);
    }
}
