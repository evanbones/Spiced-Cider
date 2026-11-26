package com.evandev.spicedcider.compat.everycompat;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class BlockBoxEveryCompatAliases {
    public static void register() {
        alias(BuiltInRegistries.BLOCK);
        alias(BuiltInRegistries.ITEM);
    }

    private static void alias(Registry<?> registry) {
        for (ResourceLocation id : registry.keySet()) {
            if (id.getNamespace().equals("everycomp") && id.getPath().startsWith("bb/")) {
                registry.addAlias(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, id.getPath()), id);
            }
        }
    }
}
