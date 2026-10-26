package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootTable;

public class MonumentChests {
    public static final ResourceKey<LootTable> LOOT = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "chests/ocean_monument"));
    private static final ResourceLocation PRISMARINE_CHEST = ResourceLocation.fromNamespaceAndPath("quark", "prismarine_chest");

    public static BlockState chest(Direction facing) {
        BlockState state = BuiltInRegistries.BLOCK.getOptional(PRISMARINE_CHEST).orElse(Blocks.CHEST).defaultBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        return state;
    }

    public static void fill(WorldGenLevel level, BoundingBox box, RandomSource random, BlockPos pos) {
        if (box.isInside(pos)) RandomizableContainer.setBlockEntityLootTable(level, random, pos, LOOT);
    }
}
