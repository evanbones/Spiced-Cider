package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBuildingBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModTags;
import com.evandev.spicedcider.registry.ModTierItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, SpicedCider.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ModTags.Items.TIDE_HOOKS)
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "grappling_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "sticky_grappling_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "flux_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "gold_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "heavy_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "light_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "double_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "adamantite_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "prismarite_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "featherlight_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "necromium_hook"));

        this.tag(ItemTags.SAPLINGS).add(ModItems.GLOAMWOOD_SAPLING.get());

        this.tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("tide", "fishing_rods")))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "adamantite_fishing_rod"));

        this.tag(Tags.Items.TOOLS)
                .add(ModItems.FIRE_STRIKER.get());

        this.tag(Tags.Items.TOOLS)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(Tags.Items.MELEE_WEAPON_TOOLS)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.SWORD_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.WEAPON_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get());
        this.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get())
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(ModItems.INFERNITE_CLEAVER.get())
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());

        this.tag(Tags.Items.ARMORS)
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE)
                .add(ModItems.MISCHIEF_HELMET.get())
                .add(ModItems.MISCHIEF_CHESTPLATE.get())
                .add(ModItems.MISCHIEF_LEGGINGS.get())
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.MISCHIEF_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.MISCHIEF_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR)
                .add(ModItems.MISCHIEF_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.MISCHIEF_BOOTS.get());
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_HELMET.get());
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_CHESTPLATE.get());
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_LEGGINGS.get());
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.MISCHIEF_BOOTS.get());

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "ingots")))
                .add(ModItems.CAST_IRON_INGOT.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "ingots/cast_iron")))
                .add(ModItems.CAST_IRON_INGOT.get());

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "nuggets")))
                .add(ModItems.CAST_IRON_NUGGET.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "nuggets/cast_iron")))
                .add(ModItems.CAST_IRON_NUGGET.get());

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks")))
                .add(ModItems.CAST_IRON_BLOCK.get())
                .add(ModItems.TALLOW_BLOCK.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/tallow")))
                .add(ModItems.TALLOW_BLOCK.get());

        this.tag(ModTags.Items.WAX_OR_TALLOW)
                .add(Items.HONEYCOMB)
                .add(ModItems.TALLOW.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "cooking_oil")))
                .add(ModItems.TALLOW.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cast_iron")))
                .add(ModItems.CAST_IRON_BLOCK.get());

        this.tag(ItemTags.STAIRS).add(ModItems.GLOWSTONE_BRICK_STAIRS.get());
        this.tag(ItemTags.SLABS).add(ModItems.GLOWSTONE_BRICK_SLAB.get());
        this.tag(ItemTags.WALLS).add(ModItems.GLOWSTONE_BRICK_WALL.get());
        if (ModItems.POLISHED_GLOWSTONE_STAIRS != null) {
            this.tag(ItemTags.STAIRS).addOptional(ModItems.POLISHED_GLOWSTONE_STAIRS.getId());
            this.tag(ItemTags.SLABS).addOptional(ModItems.POLISHED_GLOWSTONE_SLAB.getId());
            this.tag(ItemTags.WALLS).addOptional(ModItems.POLISHED_GLOWSTONE_WALL.getId());
        }
        for (ModBuildingBlocks.Def def : ModBuildingBlocks.DEFS) {
            switch (def.kind()) {
                case STAIRS -> this.tag(ItemTags.STAIRS).addOptional(def.location());
                case SLAB -> this.tag(ItemTags.SLABS).addOptional(def.location());
                case WALL -> this.tag(ItemTags.WALLS).addOptional(def.location());
                default -> {
                }
            }
            if ("brick_and_mortar".equals(def.mod())) {
                this.tag(masonryTag(def.location().getPath())).addOptional(def.location());
            }
        }
        if (ModItems.TWISTED_SIGN != null) {
            this.tag(ItemTags.SIGNS).addOptional(ModItems.TWISTED_SIGN.getId());
            this.tag(ItemTags.HANGING_SIGNS).addOptional(ModItems.TWISTED_HANGING_SIGN.getId());
            this.tag(ItemTags.BOATS).addOptional(ModItems.TWISTED_BOAT.getId());
            this.tag(ItemTags.CHEST_BOATS).addOptional(ModItems.TWISTED_CHEST_BOAT.getId());
        }
        ModItems.WOOL_STAIRS.values().forEach(item -> this.tag(ItemTags.STAIRS).add(item.get()));
        ModItems.WOOL_SLABS.values().forEach(item -> this.tag(ItemTags.SLABS).add(item.get()));

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates")))
                .add(ModItems.CAST_IRON_SHEET.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates/cast_iron")))
                .add(ModItems.CAST_IRON_SHEET.get());

        addTierItemTags();
    }

    private void addTierItemTags() {
        Item steelHammer = ModTierItems.STEEL_HAMMER.get();
        Item electrumHammer = ModTierItems.ELECTRUM_HAMMER.get();
        Item pickaxe = ModTierItems.ELECTRUM_PICKAXE.get();
        Item knife = ModTierItems.ELECTRUM_KNIFE.get();
        Item lance = ModTierItems.ELECTRUM_LANCE.get();
        Item steelShield = ModTierItems.STEEL_SHIELD.get();
        Item electrumShield = ModTierItems.ELECTRUM_SHIELD.get();
        Item helmet = ModTierItems.ELECTRUM_HELMET.get();
        Item chestplate = ModTierItems.ELECTRUM_CHESTPLATE.get();
        Item leggings = ModTierItems.ELECTRUM_LEGGINGS.get();
        Item boots = ModTierItems.ELECTRUM_BOOTS.get();

        for (TagKey<Item> tag : List.of(externalTag("manual_labour", "hammers"), externalTag("c", "tools/hammer"),
                ItemTags.CLUSTER_MAX_HARVESTABLES, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE)) {
            this.tag(tag).add(steelHammer, electrumHammer);
        }
        this.tag(Tags.Items.MINING_TOOL_TOOLS).add(steelHammer, electrumHammer, pickaxe);
        this.tag(Tags.Items.TOOLS).add(steelHammer, electrumHammer, knife);

        this.tag(ItemTags.PICKAXES).add(pickaxe);
        this.tag(ItemTags.AXES).add(ModTierItems.ELECTRUM_AXE.get());
        this.tag(ItemTags.SHOVELS).add(ModTierItems.ELECTRUM_SHOVEL.get());
        this.tag(ItemTags.HOES).add(ModTierItems.ELECTRUM_HOE.get());
        this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(pickaxe);

        this.tag(externalTag("farmersdelight", "tools/knives")).add(knife);
        this.tag(externalTag("c", "tools/knife")).add(knife);

        this.tag(externalTag("minecraft", "spears")).add(lance);
        this.tag(externalTag("minecraft", "lunge_enchantable")).add(lance);
        this.tag(Tags.Items.TOOLS_SPEAR).add(lance);
        this.tag(Tags.Items.MELEE_WEAPON_TOOLS).add(lance);
        for (TagKey<Item> tag : List.of(ItemTags.SWORD_ENCHANTABLE, ItemTags.SHARP_WEAPON_ENCHANTABLE,
                ItemTags.WEAPON_ENCHANTABLE, ItemTags.FIRE_ASPECT_ENCHANTABLE)) {
            this.tag(tag).add(lance);
        }

        this.tag(Tags.Items.TOOLS_SHIELD).add(steelShield, electrumShield);

        this.tag(ItemTags.DURABILITY_ENCHANTABLE).add(steelHammer, electrumHammer, knife, lance, steelShield, electrumShield,
                helmet, chestplate, leggings, boots);
        this.tag(ItemTags.VANISHING_ENCHANTABLE).add(helmet, chestplate, leggings, boots);

        this.tag(Tags.Items.ARMORS).add(helmet, chestplate, leggings, boots);
        this.tag(ItemTags.ARMOR_ENCHANTABLE).add(helmet, chestplate, leggings, boots);
        this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE).add(helmet, chestplate, leggings, boots);
        this.tag(ItemTags.HEAD_ARMOR).add(helmet);
        this.tag(ItemTags.CHEST_ARMOR).add(chestplate);
        this.tag(ItemTags.LEG_ARMOR).add(leggings);
        this.tag(ItemTags.FOOT_ARMOR).add(boots);
        this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add(helmet);
        this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add(chestplate);
        this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add(leggings);
        this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add(boots);

        ModTierItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.getId().getPath().startsWith("electrum_"))
                .forEach(holder -> this.tag(ItemTags.PIGLIN_LOVED).add(holder.get()));
    }

    private static TagKey<Item> externalTag(String namespace, String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static TagKey<Item> masonryTag(String path) {
        String family = Stream.of("resin", "purpur", "algal", "ash", "nether")
                .filter(path::contains)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Brick and Mortar masonry family for " + path));
        String soul = path.contains("soul_") ? "soul_" : "";
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("brick_and_mortar", soul + family + "_masonry"));
    }
}