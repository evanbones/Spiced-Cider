package com.evandev.spicedcider.compat.farmersdelight;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public final class FarmersDelightCompat {

    public static final String MOD_ID = "farmersdelight";

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static Block createFungusColony(ResourceLocation fungus, int baseLight) {
        return FarmersDelightBridge.createFungusColony(fungus, baseLight);
    }

    public static Item createColonyItem(Block block, Item.Properties properties) {
        return FarmersDelightBridge.createColonyItem(block, properties);
    }
}
