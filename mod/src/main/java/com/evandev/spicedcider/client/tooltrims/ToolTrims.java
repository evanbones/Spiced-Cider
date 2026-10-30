package com.evandev.spicedcider.client.tooltrims;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;

public final class ToolTrims {

    private static final String[] TOOL_SUFFIXES = {"_javelin", "_spear", "_hammer", "_knife", "_mattock", "_cleaver", "_sickle"};

    public static boolean candidate(Item item) {
        if (item instanceof TieredItem) return true;
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        for (String suffix : TOOL_SUFFIXES) {
            if (path.endsWith(suffix)) return true;
        }
        return false;
    }

    public static ResourceLocation overlay(ResourceLocation item, ResourceLocation pattern) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID,
                "trims/tools/" + item.getNamespace() + "/" + item.getPath() + "/" + pattern.getNamespace() + "/" + pattern.getPath());
    }

    public static ResourceLocation armorOverlay(ResourceLocation item, ResourceLocation pattern) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID,
                "trims/armor/" + item.getNamespace() + "/" + item.getPath() + "/" + pattern.getNamespace() + "/" + pattern.getPath());
    }
}
