package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModBuildingBlocks;
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

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.WORMY_DIRT.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.WORM_CRATE.get())
                .add(ModBlocks.GOLDEN_WORM_CRATE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DEPOSIT.get())
                .add(ModBlocks.DEEPSLATE_DEPOSIT.get())
                .add(ModBlocks.BLACKSTONE_DEPOSIT.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.CAST_IRON_BLOCK.get());

        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks")))
                .add(ModBlocks.CAST_IRON_BLOCK.get())
                .add(ModBlocks.TALLOW_BLOCK.get());
        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/tallow")))
                .add(ModBlocks.TALLOW_BLOCK.get());
        this.tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/cast_iron")))
                .add(ModBlocks.CAST_IRON_BLOCK.get());

        this.tag(BlockTags.STAIRS).add(ModBlocks.GLOWSTONE_BRICK_STAIRS.get());
        this.tag(BlockTags.SLABS).add(ModBlocks.GLOWSTONE_BRICK_SLAB.get());
        this.tag(BlockTags.WALLS).add(ModBlocks.GLOWSTONE_BRICK_WALL.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.GLOWSTONE_BRICKS.get())
                .add(ModBlocks.GLOWSTONE_BRICK_STAIRS.get())
                .add(ModBlocks.GLOWSTONE_BRICK_SLAB.get())
                .add(ModBlocks.GLOWSTONE_BRICK_WALL.get())
                .add(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get())
                .add(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get());

        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            this.tag(BlockTags.STAIRS).addOptional(ModBlocks.POLISHED_GLOWSTONE_STAIRS.getId());
            this.tag(BlockTags.SLABS).addOptional(ModBlocks.POLISHED_GLOWSTONE_SLAB.getId());
            this.tag(BlockTags.WALLS).addOptional(ModBlocks.POLISHED_GLOWSTONE_WALL.getId());
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .addOptional(ModBlocks.POLISHED_GLOWSTONE_STAIRS.getId())
                    .addOptional(ModBlocks.POLISHED_GLOWSTONE_SLAB.getId())
                    .addOptional(ModBlocks.POLISHED_GLOWSTONE_WALL.getId());
        }

        for (ModBuildingBlocks.Def def : ModBuildingBlocks.DEFS) {
            switch (def.kind()) {
                case STAIRS -> this.tag(BlockTags.STAIRS).addOptional(def.location());
                case SLAB -> this.tag(BlockTags.SLABS).addOptional(def.location());
                case WALL -> this.tag(BlockTags.WALLS).addOptional(def.location());
                default -> {
                }
            }
            this.tag(def.tool().equals("axe") ? BlockTags.MINEABLE_WITH_AXE : BlockTags.MINEABLE_WITH_PICKAXE).addOptional(def.location());
        }

        ModBlocks.WOOL_STAIRS.values().forEach(block -> {
            this.tag(BlockTags.STAIRS).add(block.get());
            this.tag(BlockTags.WOOL).add(block.get());
            this.tag(BlockTags.DAMPENS_VIBRATIONS).add(block.get());
        });
        ModBlocks.WOOL_SLABS.values().forEach(block -> {
            this.tag(BlockTags.SLABS).add(block.get());
            this.tag(BlockTags.WOOL).add(block.get());
            this.tag(BlockTags.DAMPENS_VIBRATIONS).add(block.get());
        });

        if (ModBlocks.TWISTED_SIGN != null) {
            this.tag(BlockTags.STANDING_SIGNS).addOptional(ModBlocks.TWISTED_SIGN.getId());
            this.tag(BlockTags.WALL_SIGNS).addOptional(ModBlocks.TWISTED_WALL_SIGN.getId());
            this.tag(BlockTags.CEILING_HANGING_SIGNS).addOptional(ModBlocks.TWISTED_HANGING_SIGN.getId());
            this.tag(BlockTags.WALL_HANGING_SIGNS).addOptional(ModBlocks.TWISTED_WALL_HANGING_SIGN.getId());
        }
    }
}