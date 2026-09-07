package com.evandev.spicedcider.compat.tide;

import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.items.FishingHookItem;
import com.li64.tide.registries.items.FishingLineItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class TideBridge {

    private TideBridge() {
    }

    static Item createHookItem(Item.Properties properties, String descriptionKey) {
        return new FishingHookItem(properties, descriptionKey);
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
