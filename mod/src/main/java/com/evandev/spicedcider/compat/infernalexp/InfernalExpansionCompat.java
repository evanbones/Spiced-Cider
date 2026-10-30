package com.evandev.spicedcider.compat.infernalexp;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public final class InfernalExpansionCompat {

    public static final String MOD_ID = "infernalexp";
    public static final ResourceLocation POLISHED_GLOWSTONE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "polished_glowstone");
    public static final ResourceLocation LUMINOUS_FUNGUS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "luminous_fungus");
    public static final ResourceLocation TAB = ResourceLocation.fromNamespaceAndPath(MOD_ID, "infernalexp");

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static Block polishedGlowstone() {
        return BuiltInRegistries.BLOCK.get(POLISHED_GLOWSTONE);
    }
}
