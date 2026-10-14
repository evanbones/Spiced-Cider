package com.evandev.spicedcider.compat.architectspalette;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public final class ArchitectsPaletteCompat {

    public static final String MOD_ID = "architects_palette";
    public static final ResourceLocation TWISTED_PLANKS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "twisted_planks");

    private static Boolean loaded;

    private ArchitectsPaletteCompat() {
    }

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static Block twistedPlanks() {
        return BuiltInRegistries.BLOCK.get(TWISTED_PLANKS);
    }
}
