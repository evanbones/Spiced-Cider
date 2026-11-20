package com.evandev.spicedcider.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class SpringPoolFeature extends Feature<SpringPoolFeature.Config> {
    private static final int SPAN = 7;
    private static final int SIZE = SPAN * 2 + 1;

    public SpringPoolFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int[][] depth = shape(random);
        int[][] height = new int[SIZE][SIZE];
        boolean[][] ring = new boolean[SIZE][SIZE];
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int cells = 0;
        int surface = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                boolean inside = depth[i][k] > 0;
                if (!inside) {
                    ring[i][k] = touches(depth, i, k);
                    if (!ring[i][k]) continue;
                }
                int x = origin.getX() + i - SPAN;
                int z = origin.getZ() + k - SPAN;
                int h = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
                pos.set(x, h, z);
                if (!isGround(level, pos, config)) return false;
                pos.set(x, h + 1, z);
                if (!level.getFluidState(pos).isEmpty()) return false;
                height[i][k] = h;
                if (inside) {
                    cells++;
                    surface = Math.min(surface, h);
                    highest = Math.max(highest, h);
                }
            }
        }
        if (cells < 8 || highest - surface > 1) return false;
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                if (ring[i][k] && (height[i][k] < surface || height[i][k] > surface + 1)) return false;
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                int d = depth[i][k];
                if (d == 0) continue;
                int x = origin.getX() + i - SPAN;
                int z = origin.getZ() + k - SPAN;
                for (int y = surface + 1; y <= height[i][k] + 1; y++) {
                    pos.set(x, y, z);
                    if (!level.getBlockState(pos).isAir()) level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
                for (int y = surface - d + 1; y <= surface; y++) {
                    pos.set(x, y, z);
                    level.setBlock(pos, config.fluid, Block.UPDATE_CLIENTS);
                }
                pos.set(x, surface - d, z);
                if (!isSolid(level, pos)) {
                    level.setBlock(pos, config.wall.getState(random, pos), Block.UPDATE_CLIENTS);
                } else if (random.nextFloat() < config.bedChance && level.getBlockState(pos).is(config.ground)) {
                    level.setBlock(pos, config.bed.getState(random, pos), Block.UPDATE_CLIENTS);
                }
            }
        }

        BlockPos.MutableBlockPos side = new BlockPos.MutableBlockPos();
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                int d = depth[i][k];
                if (d == 0) continue;
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    int ni = i + direction.getStepX();
                    int nk = k + direction.getStepZ();
                    int nd = ni >= 0 && nk >= 0 && ni < SIZE && nk < SIZE ? depth[ni][nk] : 0;
                    for (int y = surface - d + 1; y <= surface; y++) {
                        if (y > surface - nd) {
                            continue;
                        }
                        side.set(origin.getX() + ni - SPAN, y, origin.getZ() + nk - SPAN);
                        if (!isSolid(level, side)) level.setBlock(side, config.wall.getState(random, side), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                if (!ring[i][k] || random.nextFloat() >= config.shoreChance) continue;
                int top = height[i][k];
                if (top != surface) continue;
                pos.set(origin.getX() + i - SPAN, top, origin.getZ() + k - SPAN);
                side.set(pos.getX(), top + 1, pos.getZ());
                if (!level.getBlockState(side).isAir() || !isSolid(level, pos)) continue;
                level.setBlock(pos, config.shore.getState(random, pos), Block.UPDATE_CLIENTS);
            }
        }
        return true;
    }

    private static int[][] shape(RandomSource random) {
        int lobes = 1 + random.nextInt(3);
        float[] cx = new float[lobes + 1];
        float[] cz = new float[lobes + 1];
        float[] radius = new float[lobes + 1];
        radius[0] = 2.0F + random.nextFloat() * 1.2F;
        for (int l = 1; l <= lobes; l++) {
            float angle = random.nextFloat() * Mth.TWO_PI;
            float distance = 1.2F + random.nextFloat() * 1.2F;
            cx[l] = Mth.cos(angle) * distance;
            cz[l] = Mth.sin(angle) * distance;
            radius[l] = 1.4F + random.nextFloat() * 1.0F;
        }
        int[][] depth = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                float dx = i - SPAN;
                float dz = k - SPAN;
                float jitter = random.nextFloat() * 0.5F;
                for (int l = 0; l <= lobes; l++) {
                    float ox = dx - cx[l];
                    float oz = dz - cz[l];
                    float r = radius[l] - jitter;
                    if (ox * ox + oz * oz <= r * r) {
                        depth[i][k] = 1;
                        break;
                    }
                }
            }
        }
        boolean[][] deep = new boolean[SIZE][SIZE];
        for (int i = 1; i < SIZE - 1; i++) {
            for (int k = 1; k < SIZE - 1; k++) {
                deep[i][k] = depth[i][k] > 0 && depth[i - 1][k] > 0 && depth[i + 1][k] > 0
                        && depth[i][k - 1] > 0 && depth[i][k + 1] > 0 && random.nextFloat() < 0.75F;
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int k = 0; k < SIZE; k++) {
                if (deep[i][k]) depth[i][k] = 2;
            }
        }
        return depth;
    }

    private static boolean touches(int[][] depth, int i, int k) {
        for (int di = -1; di <= 1; di++) {
            for (int dk = -1; dk <= 1; dk++) {
                int ni = i + di;
                int nk = k + dk;
                if (ni >= 0 && nk >= 0 && ni < SIZE && nk < SIZE && depth[ni][nk] > 0) return true;
            }
        }
        return false;
    }

    private static boolean isGround(WorldGenLevel level, BlockPos pos, Config config) {
        return level.getBlockState(pos).is(config.ground) && isSolid(level, pos);
    }

    private static boolean isSolid(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getFluidState().isEmpty() && !state.is(BlockTags.LEAVES) && state.isCollisionShapeFullBlock(level, pos);
    }

    public record Config(BlockState fluid, BlockStateProvider wall, BlockStateProvider bed, BlockStateProvider shore,
                         HolderSet<Block> ground, float bedChance, float shoreChance) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("fluid").forGetter(Config::fluid),
                BlockStateProvider.CODEC.fieldOf("wall").forGetter(Config::wall),
                BlockStateProvider.CODEC.fieldOf("bed").forGetter(Config::bed),
                BlockStateProvider.CODEC.fieldOf("shore").forGetter(Config::shore),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("ground").forGetter(Config::ground),
                Codec.floatRange(0, 1).fieldOf("bed_chance").forGetter(Config::bedChance),
                Codec.floatRange(0, 1).fieldOf("shore_chance").forGetter(Config::shoreChance)
        ).apply(instance, Config::new));
    }
}
