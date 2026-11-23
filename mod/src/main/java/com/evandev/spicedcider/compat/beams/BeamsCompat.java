package com.evandev.spicedcider.compat.beams;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public class BeamsCompat {
    public static final String CAST_IRON = SpicedCider.MOD_ID + ":cast_iron";
    public static final ResourceLocation BEAMS_TAB = ResourceLocation.fromNamespaceAndPath("beams", "beams_tab");

    public static boolean isLoaded() {
        return ModList.get().isLoaded("beams");
    }
}
