package com.evandev.spicedcider.content.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

public class PaleGarden {
    public static final ResourceKey<Biome> BIOME = ResourceKey.create(Registries.BIOME, ResourceLocation.withDefaultNamespace("pale_garden"));

    public static boolean contains(LevelReader level, BlockPos pos) {
        return level.getBiome(pos).is(BIOME);
    }
}
