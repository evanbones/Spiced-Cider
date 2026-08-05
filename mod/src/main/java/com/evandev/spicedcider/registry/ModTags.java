package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> HAMMERS = tag("hammers");
        public static final TagKey<Item> TIDE_HOOKS = tag("tide", "hooks");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name));
        }

        private static TagKey<Item> tag(String namespace, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> SITS_ON_SLABS = tag("sits_on_slabs");
        public static final TagKey<Block> MOUNTS_ON_FACING = tag("mounts_on_facing");
        public static final TagKey<Block> HANGS_FROM_CEILING = tag("hangs_from_ceiling");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name));
        }

        private static TagKey<Block> tag(String namespace, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
        }
    }
}