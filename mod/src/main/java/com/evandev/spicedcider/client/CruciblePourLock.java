package com.evandev.spicedcider.client;

import net.minecraft.client.Minecraft;

public class CruciblePourLock {
    private static boolean locked;

    public static void lock() {
        locked = true;
    }

    public static boolean isLocked() {
        if (locked && !Minecraft.getInstance().options.keyUse.isDown()) {
            locked = false;
        }
        return locked;
    }
}
