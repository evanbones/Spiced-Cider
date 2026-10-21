package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.registry.ModTreeFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class BiomeClearancePlacement extends PlacementModifier {
    public static final MapCodec<BiomeClearancePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(placement -> placement.biomes),
            Codec.intRange(0, 64).fieldOf("horizontal_radius").forGetter(placement -> placement.horizontalRadius),
            Codec.intRange(0, 64).fieldOf("vertical_radius").forGetter(placement -> placement.verticalRadius)
    ).apply(instance, BiomeClearancePlacement::new));

    private final HolderSet<Biome> biomes;
    private final int horizontalRadius;
    private final int verticalRadius;

    public BiomeClearancePlacement(HolderSet<Biome> biomes, int horizontalRadius, int verticalRadius) {
        this.biomes = biomes;
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        BiomeSource source = context.generator().getBiomeSource();
        Climate.Sampler sampler = context.getLevel().getLevel().getChunkSource().randomState().sampler();
        int h = QuartPos.fromBlock(horizontalRadius);
        int v = QuartPos.fromBlock(verticalRadius);
        int qx = QuartPos.fromBlock(pos.getX());
        int qy = QuartPos.fromBlock(pos.getY());
        int qz = QuartPos.fromBlock(pos.getZ());
        for (int dy = -v; dy <= v; dy++) {
            for (int dx = -h; dx <= h; dx++) {
                for (int dz = -h; dz <= h; dz++) {
                    if (!biomes.contains(source.getNoiseBiome(qx + dx, qy + dy, qz + dz, sampler))) return Stream.empty();
                }
            }
        }
        return Stream.of(pos);
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModTreeFeatures.BIOME_CLEARANCE.get();
    }
}
