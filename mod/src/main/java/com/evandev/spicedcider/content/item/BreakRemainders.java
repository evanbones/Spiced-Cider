package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BreakRemainders {

    @Nullable
    public static Pair<ResourceLocation, ResourceLocation> parse(String entry) {
        int split = entry.indexOf('=');
        if (split <= 0) return null;
        ResourceLocation broken = ResourceLocation.tryParse(entry.substring(0, split).trim());
        ResourceLocation remainder = ResourceLocation.tryParse(entry.substring(split + 1).trim());
        return broken == null || remainder == null ? null : Pair.of(broken, remainder);
    }

    public static ItemStack remainderFor(Item broken) {
        List<? extends String> entries = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.breakRemainders, List.of());
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(broken);
        for (String entry : entries) {
            Pair<ResourceLocation, ResourceLocation> pair = parse(entry);
            if (pair != null && pair.getLeft().equals(id)) {
                return BuiltInRegistries.ITEM.getOptional(pair.getRight()).map(ItemStack::new).orElse(ItemStack.EMPTY);
            }
        }
        return ItemStack.EMPTY;
    }
}
