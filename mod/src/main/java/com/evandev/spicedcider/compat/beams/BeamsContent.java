package com.evandev.spicedcider.compat.beams;

import com.evandev.spicedcider.SpicedCider;
import com.syl.beams.items.BeamItem;
import com.syl.beams.registry.BeamsWoodTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class BeamsContent {
    public static Item castIronBeam() {
        return new BeamItem(new Item.Properties().stacksTo(64), BeamsCompat.CAST_IRON);
    }

    public static void registerCastIron(Supplier<Item> item) {
        BeamsWoodTypeRegistry.register(BeamsCompat.CAST_IRON, item.get(), ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/cast_iron_beam"));
    }
}
