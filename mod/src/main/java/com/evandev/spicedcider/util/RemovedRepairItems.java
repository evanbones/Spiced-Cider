package com.evandev.spicedcider.util;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class RemovedRepairItems {

    public static boolean accepts(Ingredient ingredient, ItemStack repair) {
        if (repair.isEmpty() || !ReliableRemoverCompat.isLoaded()) return false;
        for (ItemStack stack : ingredient.getItems()) {
            Item item = stack.getItem();
            if (!ReliableRemoverCompat.isRemoved(item)) continue;
            TagKey<Item> tag = unifiedTag(BuiltInRegistries.ITEM.getKey(item));
            if (tag != null && repair.is(tag) && !ReliableRemoverCompat.isRemoved(repair.getItem())) return true;
        }
        return false;
    }

    private static TagKey<Item> unifiedTag(ResourceLocation id) {
        String path = id.getPath();
        if (path.endsWith("_ingot")) return tag("ingots/" + path.substring(0, path.length() - 6));
        if (path.endsWith("_gem")) return tag("gems/" + path.substring(0, path.length() - 4));
        return null;
    }

    private static TagKey<Item> tag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
