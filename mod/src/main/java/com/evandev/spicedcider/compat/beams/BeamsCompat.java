package com.evandev.spicedcider.compat.beams;

import com.evandev.spicedcider.SpicedCider;
import com.syl.beams.items.BeamItem;
import com.syl.beams.registry.BeamsWoodTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;

import java.util.function.Supplier;

public class BeamsCompat {
    public static final String CAST_IRON = SpicedCider.MOD_ID + ":cast_iron";
    public static final ResourceLocation BEAMS_TAB = ResourceLocation.fromNamespaceAndPath("beams", "beams_tab");

    public static boolean isLoaded() {
        return ModList.get().isLoaded("beams");
    }

    public static Item castIronBeam() {
        return new BeamItem(new Item.Properties().stacksTo(64), CAST_IRON);
    }

    public static void registerCastIron(Supplier<Item> item) {
        BeamsWoodTypeRegistry.register(CAST_IRON, item.get(), ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/cast_iron_beam"));
    }
}
