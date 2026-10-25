package com.evandev.spicedcider.compat.tide;

import com.li64.tide.data.fishing.mediums.FishingMedium;
import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.items.FishingHookItem;
import com.li64.tide.registries.items.FishingLineItem;
import com.li64.tide.registries.items.TideFishingRodItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class TideBridge {

    static Item createHookItem(Item.Properties properties, String descriptionKey) {
        return new FishingHookItem(properties, descriptionKey);
    }

    static FishingRodItem createFishingRod(int baitSlots, double durability, Item.Properties properties) {
        return new TideFishingRodItem(baitSlots, durability, properties);
    }

    static void registerDimensionalTearsMedium() {
        if (FishingMedium.MEDIUMS.stream().noneMatch(medium -> medium instanceof DimensionalTearsMedium)) {
            FishingMedium.register(new DimensionalTearsMedium());
        }
    }

    static ItemStack getLine(ItemStack rod) {
        return CustomRodManager.getLine(rod);
    }

    static ItemStack getBobber(ItemStack rod) {
        return CustomRodManager.getBobber(rod);
    }

    static String getLineColor(ItemStack line) {
        return FishingLineItem.getColor(line);
    }
}
