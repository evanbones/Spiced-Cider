package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SpicedCider.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CAST_IRON_BLOCK.get())
                .addOptional(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "tuff_tiles"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "tuff_tile_stairs"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "tuff_tile_slab"));

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.CAST_IRON_BLOCK.get());

        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks")))
                .add(ModBlocks.CAST_IRON_BLOCK.get());
        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cast_iron")))
                .add(ModBlocks.CAST_IRON_BLOCK.get());
    }
}