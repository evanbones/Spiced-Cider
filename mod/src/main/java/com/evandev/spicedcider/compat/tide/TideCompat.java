package com.evandev.spicedcider.compat.tide;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class TideCompat {

    public static final String MOD_ID = "tide";

    private static final String DEFAULT_LINE_COLOR = "#FFFFFF";

    private static Boolean loaded;

    private TideCompat() {
    }

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static Item createHookItem(Item.Properties properties, String descriptionKey) {
        return TideBridge.createHookItem(properties, descriptionKey);
    }

    public static ItemStack getLine(ItemStack rod) {
        return isLoaded() ? TideBridge.getLine(rod) : ItemStack.EMPTY;
    }

    public static ItemStack getBobber(ItemStack rod) {
        return isLoaded() ? TideBridge.getBobber(rod) : ItemStack.EMPTY;
    }

    public static String getLineColor(ItemStack line) {
        return isLoaded() ? TideBridge.getLineColor(line) : DEFAULT_LINE_COLOR;
    }
}
