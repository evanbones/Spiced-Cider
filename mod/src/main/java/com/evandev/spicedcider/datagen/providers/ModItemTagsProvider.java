package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBuildingBlocks;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, SpicedCider.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(ModTags.Items.TIDE_HOOKS)
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "grappling_hook"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "sticky_grappling_hook"));

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
        }
        ModItems.WOOL_STAIRS.values().forEach(item -> this.tag(ItemTags.STAIRS).add(item.get()));
        ModItems.WOOL_SLABS.values().forEach(item -> this.tag(ItemTags.SLABS).add(item.get()));

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates")))
                .add(ModItems.CAST_IRON_SHEET.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates/cast_iron")))
                .add(ModItems.CAST_IRON_SHEET.get());
    }
}