package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SpicedCider.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.CAST_IRON_BLOCK.get(), cubeAll(ModBlocks.CAST_IRON_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.TALLOW_BLOCK.get(), cubeAll(ModBlocks.TALLOW_BLOCK.get()));

        ResourceLocation glowstoneBricks = modLoc("block/glowstone_bricks");
        simpleBlockWithItem(ModBlocks.GLOWSTONE_BRICKS.get(), cubeAll(ModBlocks.GLOWSTONE_BRICKS.get()));
        simpleBlockWithItem(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get(), cubeAll(ModBlocks.CHISELED_GLOWSTONE_BRICKS.get()));
        stairsBlock(ModBlocks.GLOWSTONE_BRICK_STAIRS.get(), glowstoneBricks);
        slabBlock(ModBlocks.GLOWSTONE_BRICK_SLAB.get(), glowstoneBricks, glowstoneBricks);
        wallBlock(ModBlocks.GLOWSTONE_BRICK_WALL.get(), glowstoneBricks);
        simpleBlockItem(ModBlocks.GLOWSTONE_BRICK_STAIRS.get(), new ModelFile.UncheckedModelFile(modLoc("block/glowstone_brick_stairs")));
        simpleBlockItem(ModBlocks.GLOWSTONE_BRICK_SLAB.get(), new ModelFile.UncheckedModelFile(modLoc("block/glowstone_brick_slab")));
        itemModels().wallInventory("glowstone_brick_wall", glowstoneBricks);
        axisBlock(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get(), modLoc("block/polished_glowstone_pillar"), modLoc("block/polished_glowstone_pillar_top"));
        simpleBlockItem(ModBlocks.POLISHED_GLOWSTONE_PILLAR.get(), new ModelFile.UncheckedModelFile(modLoc("block/polished_glowstone_pillar")));

        if (ModBlocks.POLISHED_GLOWSTONE_STAIRS != null) {
            ResourceLocation polished = ResourceLocation.fromNamespaceAndPath(InfernalExpansionCompat.MOD_ID, "block/polished_glowstone");
            stairsBlock(ModBlocks.POLISHED_GLOWSTONE_STAIRS.get(), polished);
            slabBlock(ModBlocks.POLISHED_GLOWSTONE_SLAB.get(), polished, polished);
            wallBlock(ModBlocks.POLISHED_GLOWSTONE_WALL.get(), polished);
            simpleBlockItem(ModBlocks.POLISHED_GLOWSTONE_STAIRS.get(), new ModelFile.UncheckedModelFile(modLoc("block/polished_glowstone_stairs")));
            simpleBlockItem(ModBlocks.POLISHED_GLOWSTONE_SLAB.get(), new ModelFile.UncheckedModelFile(modLoc("block/polished_glowstone_slab")));
            itemModels().wallInventory("polished_glowstone_wall", polished);
        }

        for (DyeColor color : DyeColor.values()) {
            ResourceLocation wool = mcLoc("block/" + color.getName() + "_wool");
            StairBlock stairs = ModBlocks.WOOL_STAIRS.get(color).get();
            SlabBlock slab = ModBlocks.WOOL_SLABS.get(color).get();
            stairsBlock(stairs, wool);
            slabBlock(slab, wool, wool);
            simpleBlockItem(stairs, new ModelFile.UncheckedModelFile(modLoc("block/" + color.getName() + "_wool_stairs")));
            simpleBlockItem(slab, new ModelFile.UncheckedModelFile(modLoc("block/" + color.getName() + "_wool_slab")));
        }
    }
}