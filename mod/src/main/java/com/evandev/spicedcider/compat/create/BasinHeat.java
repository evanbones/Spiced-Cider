package com.evandev.spicedcider.compat.create;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BasinHeat {
    public static final TagKey<Block> HEAT_SOURCES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("farmersdelight", "heat_sources"));

    public static boolean isHeatSource(BlockState state) {
        return state.is(HEAT_SOURCES) && (!state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT));
    }
}
