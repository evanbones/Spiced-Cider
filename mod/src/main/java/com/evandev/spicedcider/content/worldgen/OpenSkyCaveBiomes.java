package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Map;
import java.util.WeakHashMap;

public class OpenSkyCaveBiomes {
    public static final TagKey<Biome> CAVE_BIOMES = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "no_open_sky"));
    private static final int MARGIN = 6;
    private static final float[] SURFACE_DEPTHS = {0.0F, -0.15F, -0.3F};
    private static final Map<Climate.Sampler, Climate.Sampler[]> SURFACE_SAMPLERS = new WeakHashMap<>();

    public static ChunkAccess apply(ServerLevel level, ChunkAccess chunk) {
        if (!(chunk instanceof ProtoChunk) || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.noOpenSkyCaveBiomes, false)) return chunk;
        ChunkPos pos = chunk.getPos();
        Heightmap floor = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        int[] limit = new int[16];
        int lowest = Integer.MAX_VALUE;
        for (int qx = 0; qx < 4; qx++) {
            for (int qz = 0; qz < 4; qz++) {
                int min = Integer.MAX_VALUE;
                for (int dx = 0; dx < 4; dx++) {
                    for (int dz = 0; dz < 4; dz++) {
                        min = Math.min(min, floor.getFirstAvailable(qx * 4 + dx, qz * 4 + dz));
                    }
                }
                limit[qx * 4 + qz] = min - MARGIN;
                lowest = Math.min(lowest, limit[qx * 4 + qz]);
            }
        }
        BiomeSource source = level.getChunkSource().getGenerator().getBiomeSource();
        Climate.Sampler[] samplers = surfaceSamplers(level.getChunkSource().randomState().sampler());
        int baseQx = QuartPos.fromBlock(pos.getMinBlockX());
        int baseQz = QuartPos.fromBlock(pos.getMinBlockZ());
        LevelChunkSection[] sections = chunk.getSections();
        for (int index = 0; index < sections.length; index++) {
            int sectionY = chunk.getSectionYFromSectionIndex(index);
            int topBlock = (sectionY << 4) + 15;
            if (topBlock < lowest) continue;
            int baseQy = QuartPos.fromSection(sectionY);
            if (!needsFix(chunk, baseQx, baseQy, baseQz, limit)) continue;
            sections[index].fillBiomesFromNoise((qx, qy, qz, s) -> {
                Holder<Biome> current = chunk.getNoiseBiome(qx, qy, qz);
                if (!exposed(qx - baseQx, qy, qz - baseQz, limit) || !current.is(CAVE_BIOMES)) return current;
                for (Climate.Sampler surfaceSampler : samplers) {
                    Holder<Biome> surface = source.getNoiseBiome(qx, qy, qz, surfaceSampler);
                    if (!surface.is(CAVE_BIOMES)) return surface;
                }
                return current;
            }, samplers[0], baseQx, baseQy, baseQz);
        }
        return chunk;
    }

    private static boolean needsFix(ChunkAccess chunk, int baseQx, int baseQy, int baseQz, int[] limit) {
        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                for (int y = 0; y < 4; y++) {
                    if (exposed(x, baseQy + y, z, limit) && chunk.getNoiseBiome(baseQx + x, baseQy + y, baseQz + z).is(CAVE_BIOMES)) return true;
                }
            }
        }
        return false;
    }

    private static boolean exposed(int localQx, int qy, int localQz, int[] limit) {
        return QuartPos.toBlock(qy) + 3 >= limit[localQx * 4 + localQz];
    }

    private static Climate.Sampler[] surfaceSamplers(Climate.Sampler sampler) {
        synchronized (SURFACE_SAMPLERS) {
            return SURFACE_SAMPLERS.computeIfAbsent(sampler, s -> {
                Climate.Sampler[] samplers = new Climate.Sampler[SURFACE_DEPTHS.length];
                for (int i = 0; i < samplers.length; i++) {
                    samplers[i] = new Climate.Sampler(s.temperature(), s.humidity(), s.continentalness(), s.erosion(), DensityFunctions.constant(SURFACE_DEPTHS[i]), s.weirdness(), s.spawnTarget());
                }
                return samplers;
            });
        }
    }
}
