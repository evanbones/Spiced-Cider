package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {

    protected ModBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.CAST_IRON_BLOCK.get());
        this.dropSelf(ModBlocks.TALLOW_BLOCK.get());
        this.dropSelf(ModBlocks.GLOWSTONE_BRICKS.get());
        this.dropSelf(ModBlocks.GLOWSTONE_BRICK_STAIRS.get());
        this.add(ModBlocks.GLOWSTONE_BRICK_SLAB.get(), this::createSlabItemTable);
        this.dropSelf(ModBlocks.GLOWSTONE_BRICK_WALL.get());
        this.dropSelf(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get());
        this.dropSelf(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get());
        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            this.dropSelf(ModBlocks.POLISHED_GLOWSTONE_STAIRS.get());
            this.add(ModBlocks.POLISHED_GLOWSTONE_SLAB.get(), this::createSlabItemTable);
            this.dropSelf(ModBlocks.POLISHED_GLOWSTONE_WALL.get());
        }
        if (ModBlocks.TWISTED_SIGN != null) {
            this.dropSelf(ModBlocks.TWISTED_SIGN.get());
            this.dropSelf(ModBlocks.TWISTED_HANGING_SIGN.get());
        }
        ModBlocks.WOOL_STAIRS.values().forEach(block -> this.dropSelf(block.get()));
        ModBlocks.WOOL_SLABS.values().forEach(block -> this.add(block.get(), this::createSlabItemTable));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        List<Block> blocks = new ArrayList<>(List.of(ModBlocks.CAST_IRON_BLOCK.get(), ModBlocks.TALLOW_BLOCK.get()));
        blocks.add(ModBlocks.GLOWSTONE_BRICKS.get());
        blocks.add(ModBlocks.GLOWSTONE_BRICK_STAIRS.get());
        blocks.add(ModBlocks.GLOWSTONE_BRICK_SLAB.get());
        blocks.add(ModBlocks.GLOWSTONE_BRICK_WALL.get());
        blocks.add(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get());
        blocks.add(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get());
        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            blocks.add(ModBlocks.POLISHED_GLOWSTONE_STAIRS.get());
            blocks.add(ModBlocks.POLISHED_GLOWSTONE_SLAB.get());
            blocks.add(ModBlocks.POLISHED_GLOWSTONE_WALL.get());
        }
        if (ModBlocks.TWISTED_SIGN != null) {
            blocks.add(ModBlocks.TWISTED_SIGN.get());
            blocks.add(ModBlocks.TWISTED_WALL_SIGN.get());
            blocks.add(ModBlocks.TWISTED_HANGING_SIGN.get());
            blocks.add(ModBlocks.TWISTED_WALL_HANGING_SIGN.get());
        }
        ModBlocks.WOOL_STAIRS.values().forEach(block -> blocks.add(block.get()));
        ModBlocks.WOOL_SLABS.values().forEach(block -> blocks.add(block.get()));
        return blocks;
    }
}