package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
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
        this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
                .add(ModItems.FLINT_HAMMER.get())
                .add(ModItems.IRON_HAMMER.get())
                .add(ModItems.GOLDEN_HAMMER.get())
                .add(ModItems.DIAMOND_HAMMER.get())
                .add(ModItems.NETHERITE_HAMMER.get());

        this.tag(ModTags.Items.HAMMERS)
                .add(ModItems.FLINT_HAMMER.get())
                .add(ModItems.IRON_HAMMER.get())
                .add(ModItems.GOLDEN_HAMMER.get())
                .add(ModItems.DIAMOND_HAMMER.get())
                .add(ModItems.NETHERITE_HAMMER.get());

        this.tag(ModTags.Items.TIDE_HOOKS)
                .add(ModItems.GRAPPLING_HOOK.get())
                .add(ModItems.STICKY_GRAPPLING_HOOK.get());

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
                .add(ModItems.CAST_IRON_BLOCK.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cast_iron")))
                .add(ModItems.CAST_IRON_BLOCK.get());

        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates")))
                .add(ModItems.CAST_IRON_PLATE.get());
        this.tag(ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "plates/cast_iron")))
                .add(ModItems.CAST_IRON_PLATE.get());
    }
}