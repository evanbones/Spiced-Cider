package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SpicedCider.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile workstoneModel = models().withExistingParent("workstone", mcLoc("block/block"))
                .texture("particle", modLoc("block/workstone_top"))
                .texture("bottom", modLoc("block/workstone_bottom"))
                .texture("top", modLoc("block/workstone_top"))
                .texture("front", modLoc("block/workstone_front"))
                .texture("side", modLoc("block/workstone_side"))
                .element()
                .from(3, 0, 3).to(13, 12, 13)
                .face(Direction.DOWN).texture("#bottom").end()
                .face(Direction.UP).texture("#top").end()
                .face(Direction.NORTH).texture("#front").end()
                .face(Direction.SOUTH).texture("#front").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end()
                .end();

        horizontalBlock(ModBlocks.WORKSTONE.get(), workstoneModel);
        simpleBlockItem(ModBlocks.WORKSTONE.get(), workstoneModel);
    }
}