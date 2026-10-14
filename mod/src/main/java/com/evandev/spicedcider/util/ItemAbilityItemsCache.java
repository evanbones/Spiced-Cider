package com.evandev.spicedcider.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemAbilityItemsCache {
    private static final Map<ItemAbility, List<Item>> ITEMS = new ConcurrentHashMap<>();

    private ItemAbilityItemsCache() {
    }

    public static List<Item> items(ItemAbility ability) {
        return ITEMS.computeIfAbsent(ability, a -> BuiltInRegistries.ITEM.stream()
                .filter(item -> new ItemStack(item).canPerformAction(a))
                .toList());
    }

    public static void clear() {
        ITEMS.clear();
    }
}
