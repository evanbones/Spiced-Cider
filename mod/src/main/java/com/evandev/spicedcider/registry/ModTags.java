package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> HAMMERS = tag("hammers");
        public static final TagKey<Item> TIDE_HOOKS = tag("tide", "hooks");

        public static final TagKey<Item> KEPT_WHEN_BROKEN = tag("kept_when_broken");
        public static final TagKey<Item> DESTROYED_WHEN_BROKEN = tag("destroyed_when_broken");
        public static final TagKey<Item> WAX_OR_TALLOW = tag("wax_or_tallow");
        public static final TagKey<Item> TANNABLE_HIDES = tag("tannable_hides");
        public static final TagKey<Item> LARGE_TANNABLE_HIDES = tag("large_tannable_hides");
        public static final TagKey<Item> TANNING_AGENTS = tag("tanning_agents");
        public static final TagKey<Item> WORM_SEEKER_ITEMS = tag("worm_seeker_items");
        public static final TagKey<Item> BALLOONS = tag("balloons");
        public static final TagKey<Item> CACAO_LOGS = tag("cacao_logs");
        public static final TagKey<Item> PEAR_LOGS = tag("pear_logs");
        public static final TagKey<Item> AVOCADO_LOGS = tag("avocado_logs");
        public static final TagKey<Item> STRAW_GOLEM_HATS = tag("straw_golem_hats");
        public static final TagKey<Item> SULFUR_FIRE_BASE_BLOCKS = tag("sulfur_fire_base_blocks");
        public static final TagKey<Item> SULFUR_TORCHES = tag("c", "torches/sulfur");
        public static final TagKey<Item> SULFUR_DUSTS = tag("c", "dusts/sulfur");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name));
        }

        private static TagKey<Item> tag(String namespace, String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
        }
    }

    public static class Blocks {
        public static final TagKey<Block> HANGS_FROM_CEILING = tag("hangs_from_ceiling");
        public static final TagKey<Block> TAP_SUPPORTS = tag("tap_supports");
        public static final TagKey<Block> NEEDS_AXE_TO_HARVEST = tag("needs_axe_to_harvest");
        public static final TagKey<Block> NEEDS_HOE_TO_HARVEST = tag("needs_hoe_to_harvest");
        public static final TagKey<Block> CACAO_LOGS = tag("cacao_logs");
        public static final TagKey<Block> CACAO_LEAVES = tag("cacao_leaves");
        public static final TagKey<Block> PEAR_LOGS = tag("pear_logs");
        public static final TagKey<Block> PEAR_LEAVES = tag("pear_leaves");
        public static final TagKey<Block> AVOCADO_LOGS = tag("avocado_logs");
        public static final TagKey<Block> AVOCADO_LEAVES = tag("avocado_leaves");
        public static final TagKey<Block> GROWS_PINECONES = tag("grows_pinecones");
        public static final TagKey<Block> STRAW_GOLEM_CROPS = tag("straw_golem_crops");
        public static final TagKey<Block> GOLEM_HEADS = tag("golem_heads");
        public static final TagKey<Block> SULFUR_FIRE_BASE_BLOCKS = tag("sulfur_fire_base_blocks");
        public static final TagKey<Block> PALE_GOLEM_HEADS = tag("pale_golem_heads");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name));
        }

        private static TagKey<Block> tag(String namespace, String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
        }
    }

    public static class Structures {
        public static final TagKey<Structure> DISABLED = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "disabled"));
        public static final TagKey<Structure> AVOIDS_OCEAN = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "avoids_ocean"));
    }

    public static class Biomes {
        public static final TagKey<Biome> RESTRICTED_SPAWNS = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "restricted_spawns"));
        public static final TagKey<Biome> HAS_GLOWING_FOG = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "has_glowing_fog"));
        public static final TagKey<Biome> HAS_THICK_FOG = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "has_thick_fog"));
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> DENY_SPAWN = tag("deny_spawn");
        public static final TagKey<EntityType<?>> DENY_NATURAL_SPAWN = tag("deny_natural_spawn");
        public static final TagKey<EntityType<?>> WORM_SEEKERS = tag("worm_seekers");
        public static final TagKey<EntityType<?>> ALLOWED_IN_RESTRICTED_BIOMES = tag("allowed_in_restricted_biomes");
        public static final TagKey<EntityType<?>> BALLOON_UNATTACHABLE = tag("balloon_unattachable");

        private static TagKey<EntityType<?>> tag(String name) {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name));
        }
    }
}
