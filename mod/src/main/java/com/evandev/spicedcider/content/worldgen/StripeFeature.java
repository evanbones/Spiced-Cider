package com.evandev.spicedcider.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.Optional;

public class StripeFeature extends Feature<StripeFeature.Config> {
    public StripeFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        ChunkAccess chunk = level.getChunk(context.origin());
        WorldgenRandom noiseRandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed() ^ config.noiseSeed));
        SimplexNoise bands = new SimplexNoise(noiseRandom);
        SimplexNoise warp = new SimplexNoise(noiseRandom);
        SimplexNoise mask = new SimplexNoise(noiseRandom);

        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int minY = Math.max(chunk.getMinBuildHeight(), config.minY);
        int maxY = Math.min(chunk.getMaxBuildHeight() - 1, config.maxY);
        boolean placed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos side = new BlockPos.MutableBlockPos();

        for (int qx = 0; qx < 4; qx++) {
            for (int qz = 0; qz < 4; qz++) {
                for (int qy = QuartPos.fromBlock(minY); qy <= QuartPos.fromBlock(maxY); qy++) {
                    if (!config.biomes.contains(chunk.getNoiseBiome(QuartPos.fromBlock(minX) + qx, qy, QuartPos.fromBlock(minZ) + qz))) continue;
                    for (int dx = 0; dx < 4; dx++) {
                        for (int dz = 0; dz < 4; dz++) {
                            int x = minX + qx * 4 + dx;
                            int z = minZ + qz * 4 + dz;
                            if (mask.getValue(x / config.patchScale, z / config.patchScale) < config.patchThreshold) continue;
                            double offset = warp.getValue(x / config.warpScale, z / config.warpScale) * config.warpHeight;
                            for (int dy = 0; dy < 4; dy++) {
                                int y = QuartPos.toBlock(qy) + dy;
                                if (y < minY || y > maxY) continue;
                                pos.set(x, y, z);
                                if (!chunk.getBlockState(pos).is(config.target)) continue;
                                double value = bands.getValue(x / config.horizontalScale, (y + offset) / config.verticalScale, z / config.horizontalScale);
                                if (Math.abs(value) >= config.thickness) continue;
                                level.setBlock(pos, config.state, Block.UPDATE_CLIENTS);
                                placed = true;
                                config.spike.ifPresent(spike -> growSpikes(level, random, config, spike, pos, side, minX, minZ));
                            }
                        }
                    }
                }
            }
        }
        return placed;
    }

    private static void growSpikes(WorldGenLevel level, RandomSource random, Config config, BlockState spike, BlockPos pos, BlockPos.MutableBlockPos side, int minX, int minZ) {
        for (Direction direction : Direction.values()) {
            side.setWithOffset(pos, direction);
            if (side.getX() < minX || side.getX() > minX + 15 || side.getZ() < minZ || side.getZ() > minZ + 15) continue;
            float chance = direction.getAxis() == Direction.Axis.Y ? config.spikeChance : config.spikeChance * 0.5F;
            if (random.nextFloat() >= chance) continue;
            BlockState existing = level.getBlockState(side);
            boolean water = existing.is(Blocks.WATER) && existing.getFluidState().isSource();
            if (!existing.isAir() && !water) continue;
            BlockState state = spike.trySetValue(BlockStateProperties.FACING, direction).trySetValue(BlockStateProperties.WATERLOGGED, water);
            level.setBlock(side, state, Block.UPDATE_CLIENTS);
        }
    }

    public record Config(BlockState state, Optional<BlockState> spike, TagKey<Block> target, HolderSet<Biome> biomes, int noiseSeed,
                         int minY, int maxY, double horizontalScale, double verticalScale, double thickness,
                         double warpScale, double warpHeight, double patchScale, double patchThreshold, float spikeChance) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("state").forGetter(Config::state),
                BlockState.CODEC.optionalFieldOf("spike").forGetter(Config::spike),
                TagKey.codec(Registries.BLOCK).fieldOf("target").forGetter(Config::target),
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(Config::biomes),
                Codec.INT.fieldOf("noise_seed").forGetter(Config::noiseSeed),
                Codec.INT.fieldOf("min_y").forGetter(Config::minY),
                Codec.INT.fieldOf("max_y").forGetter(Config::maxY),
                Codec.doubleRange(1, 512).fieldOf("horizontal_scale").forGetter(Config::horizontalScale),
                Codec.doubleRange(1, 512).fieldOf("vertical_scale").forGetter(Config::verticalScale),
                Codec.doubleRange(0, 1).fieldOf("thickness").forGetter(Config::thickness),
                Codec.doubleRange(1, 512).fieldOf("warp_scale").forGetter(Config::warpScale),
                Codec.doubleRange(0, 64).fieldOf("warp_height").forGetter(Config::warpHeight),
                Codec.doubleRange(1, 1024).fieldOf("patch_scale").forGetter(Config::patchScale),
                Codec.doubleRange(-1, 1).fieldOf("patch_threshold").forGetter(Config::patchThreshold),
                Codec.floatRange(0, 1).fieldOf("spike_chance").forGetter(Config::spikeChance)
        ).apply(instance, Config::new));
    }
}
