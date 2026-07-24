package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {

    protected ModBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.WORKSTONE.get());
        this.dropSelf(ModBlocks.CAST_IRON_BLOCK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return List.of(ModBlocks.WORKSTONE.get(), ModBlocks.CAST_IRON_BLOCK.get());
    }
}