package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.architectspalette.ArchitectsPaletteCompat;
import com.evandev.spicedcider.compat.infernalexp.InfernalExpansionCompat;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.content.block.pale.RedstoneJackOLanternBlock;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SpicedCider.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.CAST_IRON_BLOCK.get(), cubeAll(ModBlocks.CAST_IRON_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.TALLOW_BLOCK.get(), cubeAll(ModBlocks.TALLOW_BLOCK.get()));
        simpleBlockWithItem(ModBlocks.WORMY_DIRT.get(), cubeAll(ModBlocks.WORMY_DIRT.get()));
        simpleBlockWithItem(ModBlocks.DEPOSIT.get(), cubeAll(ModBlocks.DEPOSIT.get()));
        simpleBlockWithItem(ModBlocks.DEEPSLATE_DEPOSIT.get(), cubeAll(ModBlocks.DEEPSLATE_DEPOSIT.get()));
        simpleBlockWithItem(ModBlocks.BLACKSTONE_DEPOSIT.get(), cubeAll(ModBlocks.BLACKSTONE_DEPOSIT.get()));
        simpleBlockWithItem(ModBlocks.WORM_CRATE.get(), models().cubeBottomTop("worm_crate",
                modLoc("block/worm_crate_side"), modLoc("block/worm_crate_bottom"), modLoc("block/worm_crate_top")));
        simpleBlockWithItem(ModBlocks.GOLDEN_WORM_CRATE.get(), models().cubeBottomTop("golden_worm_crate",
                modLoc("block/worm_crate_side"), modLoc("block/worm_crate_bottom"), modLoc("block/golden_worm_crate_top")));

        ResourceLocation gloamwoodSapling = modLoc("block/gloamwood_sapling");
        simpleBlock(ModBlocks.GLOAMWOOD_SAPLING.get(), models().cross("gloamwood_sapling", gloamwoodSapling).renderType("cutout"));
        simpleBlock(ModBlocks.POTTED_GLOAMWOOD_SAPLING.get(), models().withExistingParent("potted_gloamwood_sapling", mcLoc("block/flower_pot_cross"))
                .texture("plant", gloamwoodSapling).renderType("cutout"));
        itemModels().withExistingParent("gloamwood_sapling", mcLoc("item/generated")).texture("layer0", gloamwoodSapling);

        ResourceLocation cacaoSapling = modLoc("block/cacao_sapling");
        simpleBlock(ModBlocks.CACAO_SAPLING.get(), models().cross("cacao_sapling", cacaoSapling).renderType("cutout"));
        simpleBlock(ModBlocks.POTTED_CACAO_SAPLING.get(), models().withExistingParent("potted_cacao_sapling", mcLoc("block/flower_pot_cross"))
                .texture("plant", cacaoSapling).renderType("cutout"));
        itemModels().withExistingParent("cacao_sapling", mcLoc("item/generated")).texture("layer0", cacaoSapling);

        ResourceLocation pearSapling = modLoc("block/pear_sapling");
        simpleBlock(ModBlocks.PEAR_SAPLING.get(), models().cross("pear_sapling", pearSapling).renderType("cutout"));
        simpleBlock(ModBlocks.POTTED_PEAR_SAPLING.get(), models().withExistingParent("potted_pear_sapling", mcLoc("block/flower_pot_cross"))
                .texture("plant", pearSapling).renderType("cutout"));
        itemModels().withExistingParent("pear_sapling", mcLoc("item/generated")).texture("layer0", pearSapling);

        simpleBlockWithItem(ModBlocks.DEEPSILT.get(), cubeAll(ModBlocks.DEEPSILT.get()));
        simpleBlockWithItem(ModBlocks.POLISHED_DRIED_DEEPSILT.get(), cubeAll(ModBlocks.POLISHED_DRIED_DEEPSILT.get()));
        for (var block : List.of(ModBlocks.DRIED_DEEPSILT, ModBlocks.CUT_DRIED_DEEPSILT, ModBlocks.CHISELED_DRIED_DEEPSILT)) {
            simpleBlockWithItem(block.get(), models().cubeBottomTop(block.getId().getPath(), modLoc("block/" + block.getId().getPath()),
                    modLoc("block/dried_deepsilt_bottom"), modLoc("block/dried_deepsilt_top")));
        }

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

        if (ModBlocks.TWISTED_SIGN != null) {
            ResourceLocation planks = ResourceLocation.fromNamespaceAndPath(ArchitectsPaletteCompat.MOD_ID, "block/twisted_planks");
            ResourceLocation strippedLog = ResourceLocation.fromNamespaceAndPath(ArchitectsPaletteCompat.MOD_ID, "block/stripped_twisted_log");
            signBlock(ModBlocks.TWISTED_SIGN.get(), ModBlocks.TWISTED_WALL_SIGN.get(), planks);
            hangingSignBlock(ModBlocks.TWISTED_HANGING_SIGN.get(), ModBlocks.TWISTED_WALL_HANGING_SIGN.get(), strippedLog);
        }
        paleGarden();
    }

    private void paleGarden() {
        ResourceLocation paleSide = modLoc("block/pale_pumpkin_side");
        ResourceLocation paleTop = modLoc("block/pale_pumpkin_top");
        simpleBlockWithItem(ModPaleGardenBlocks.PALE_PUMPKIN.get(), models().cubeColumn("pale_pumpkin", paleSide, paleTop));
        for (DeferredBlock<? extends Block> block : List.<DeferredBlock<? extends Block>>of(ModPaleGardenBlocks.CARVED_PALE_PUMPKIN)) {
            carved(block, paleSide, paleTop);
        }
        for (DeferredBlock<? extends Block> block : ModPaleGardenBlocks.JACK_O_LANTERNS) {
            String name = block.getId().getPath();
            boolean pale = name.startsWith("pale_");
            ResourceLocation side = pale ? paleSide : mcLoc("block/pumpkin_side");
            ResourceLocation top = pale ? paleTop : mcLoc("block/pumpkin_top");
            if (block.get() instanceof RedstoneJackOLanternBlock) {
                ModelFile off = models().orientable(name, side, modLoc("block/" + name), top);
                ModelFile on = models().orientable(name + "_on", side, modLoc("block/" + name + "_on"), top);
                horizontalBlock(block.get(), state -> state.getValue(RedstoneJackOLanternBlock.LIT) ? on : off);
                simpleBlockItem(block.get(), off);
            } else {
                carved(block, side, top);
            }
        }

        cross(ModPaleGardenBlocks.PALE_ROSE, "pale_rose");
        cross(ModPaleGardenBlocks.PALE_BUSH, "pale_bush");
        ModelFile tallBottom = models().cross("tall_pale_bush_bottom", modLoc("block/tall_pale_bush_bottom")).renderType("cutout");
        ModelFile tallTop = models().cross("tall_pale_bush_top", modLoc("block/tall_pale_bush_top")).renderType("cutout");
        getVariantBuilder(ModPaleGardenBlocks.TALL_PALE_BUSH.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? tallBottom : tallTop).build());
        itemModels().withExistingParent("tall_pale_bush", mcLoc("item/generated")).texture("layer0", modLoc("block/tall_pale_bush_top"));

        pottedCross(ModPaleGardenBlocks.POTTED_PALE_ROSE, modLoc("block/pale_rose"));
        pottedCross(ModPaleGardenBlocks.POTTED_PALE_BUSH, modLoc("block/pale_bush"));
        pottedTall(ModPaleGardenBlocks.POTTED_TALL_PALE_BUSH, modLoc("block/tall_pale_bush_bottom"), modLoc("block/tall_pale_bush_top"));
        pottedTall(ModPaleGardenBlocks.POTTED_ROSE_BUSH, mcLoc("block/rose_bush_bottom"), mcLoc("block/rose_bush_top"));
        pottedTall(ModPaleGardenBlocks.POTTED_PEONY, mcLoc("block/peony_bottom"), mcLoc("block/peony_top"));
        pottedTall(ModPaleGardenBlocks.POTTED_LILAC, mcLoc("block/lilac_bottom"), mcLoc("block/lilac_top"));
        simpleBlock(ModPaleGardenBlocks.POTTED_SUNFLOWER.get(), models().getExistingFile(modLoc("block/potted_sunflower")));
        simpleBlock(ModPaleGardenBlocks.POTTED_PITCHER_PLANT.get(), models().getExistingFile(modLoc("block/potted_pitcher_plant")));
    }

    private void carved(DeferredBlock<? extends Block> block, ResourceLocation side, ResourceLocation top) {
        String name = block.getId().getPath();
        ModelFile model = models().orientable(name, side, modLoc("block/" + name), top);
        horizontalBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }

    private void cross(DeferredBlock<? extends Block> block, String texture) {
        simpleBlock(block.get(), models().cross(block.getId().getPath(), modLoc("block/" + texture)).renderType("cutout"));
        itemModels().withExistingParent(block.getId().getPath(), mcLoc("item/generated")).texture("layer0", modLoc("block/" + texture));
    }

    private void pottedCross(DeferredBlock<? extends Block> block, ResourceLocation plant) {
        simpleBlock(block.get(), models().withExistingParent(block.getId().getPath(), mcLoc("block/flower_pot_cross"))
                .texture("plant", plant).renderType("cutout"));
    }

    private void pottedTall(DeferredBlock<? extends Block> block, ResourceLocation bottom, ResourceLocation top) {
        simpleBlock(block.get(), models().withExistingParent(block.getId().getPath(), modLoc("block/tall_flower_pot_cross"))
                .texture("plant_bottom", bottom).texture("plant_top", top).renderType("cutout"));
    }
}