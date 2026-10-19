package com.evandev.spicedcider.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class CaveRootFeature extends Feature<CaveRootFeature.Config> {
    public CaveRootFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        Config config = context.config();
        BlockPos base = context.origin();

        if (!level.getBlockState(base.below()).isFaceSturdy(level, base.below(), Direction.UP)) return false;

        int height = -1;
        BlockPos.MutableBlockPos cursor = base.mutable();
        for (int i = 0; i <= config.maxHeight; i++) {
            BlockState state = level.getBlockState(cursor);
            if (!passable(state)) {
                if (state.isFaceSturdy(level, cursor, Direction.DOWN)) height = i;
                break;
            }
            cursor.move(Direction.UP);
        }
        if (height < config.minHeight) return false;

        BlockPos top = base.above(height - 1);
        for (int i = 0; i < height; i++) {
            BlockPos pos = base.above(i);
            level.setBlock(pos, config.trunkProvider.getState(random, pos), Block.UPDATE_CLIENTS);
        }

        flare(level, random, config, base, Direction.DOWN);
        flare(level, random, config, top, Direction.UP);
        rootInto(level, random, config, base.below(), Direction.DOWN, top);
        rootInto(level, random, config, top.above(), Direction.UP, top);

        int branches = config.branchCount.sample(random);
        for (int b = 0; b < branches && height > 6; b++) {
            BlockPos from = base.above(3 + random.nextInt(height - 6));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos stub = from.relative(direction);
            if (!passable(level.getBlockState(stub))) continue;
            level.setBlock(stub, config.trunkProvider.getState(random, stub).trySetValue(RotatedPillarBlock.AXIS, direction.getAxis()), Block.UPDATE_CLIENTS);
            clump(level, random, config, stub, 4 + random.nextInt(4));
        }

        for (int i = 1; i < height - 1; i++) {
            if (random.nextFloat() < config.leafChance) {
                clump(level, random, config, base.above(i), 1 + random.nextInt(4));
            }
        }

        return true;
    }

    private static void flare(WorldGenLevel level, RandomSource random, Config config, BlockPos end, Direction toward) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() >= config.flareChance) continue;
            BlockPos pos = end.relative(direction);
            BlockPos support = pos.relative(toward);
            if (!passable(level.getBlockState(pos))) continue;
            if (!level.getBlockState(support).isFaceSturdy(level, support, toward.getOpposite())) continue;
            level.setBlock(pos, config.trunkProvider.getState(random, pos).trySetValue(RotatedPillarBlock.AXIS, direction.getAxis()), Block.UPDATE_CLIENTS);
        }
    }

    private static void rootInto(WorldGenLevel level, RandomSource random, Config config, BlockPos anchor, Direction into, BlockPos trunkEnd) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                boolean centre = dx == 0 && dz == 0;
                if (!centre && random.nextFloat() >= 0.5F) continue;
                BlockPos pos = anchor.offset(dx, 0, dz);
                int depth = centre ? 1 + random.nextInt(2) : 1;
                for (int d = 0; d < depth; d++) {
                    BlockPos target = pos.relative(into, d);
                    if (!level.getBlockState(target).is(config.rootReplaceable)) break;
                    level.setBlock(target, config.rootProvider.getState(random, target), Block.UPDATE_CLIENTS);
                }
                if (into == Direction.UP && !centre && random.nextFloat() < config.hangingRootChance) {
                    BlockPos below = pos.below();
                    BlockState hanging = config.hangingRootProvider.getState(random, below);
                    if (level.getBlockState(below).isAir() && hanging.canSurvive(level, below) && !below.equals(trunkEnd)) {
                        level.setBlock(below, hanging, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }

    private static void clump(WorldGenLevel level, RandomSource random, Config config, BlockPos log, int attempts) {
        for (int i = 0; i < attempts; i++) {
            BlockPos pos = log.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            if (random.nextInt(3) == 0) pos = pos.relative(random.nextBoolean() ? Direction.UP : Direction.DOWN);
            if (!level.getBlockState(pos).isAir()) continue;
            int distance = distance(level, pos);
            if (distance > 2) continue;
            level.setBlock(pos, config.foliageProvider.getState(random, pos).trySetValue(LeavesBlock.DISTANCE, distance), Block.UPDATE_CLIENTS);
        }
    }

    private static int distance(WorldGenLevel level, BlockPos pos) {
        int best = LeavesBlock.DECAY_DISTANCE;
        for (Direction direction : Direction.values()) {
            BlockState neighbour = level.getBlockState(pos.relative(direction));
            if (neighbour.is(BlockTags.LOGS)) return 1;
            if (neighbour.hasProperty(LeavesBlock.DISTANCE)) best = Math.min(best, neighbour.getValue(LeavesBlock.DISTANCE) + 1);
        }
        return best;
    }

    private static boolean passable(BlockState state) {
        return state.isAir() || state.is(BlockTags.REPLACEABLE_BY_TREES) && state.getFluidState().isEmpty();
    }

    public record Config(BlockStateProvider trunkProvider, BlockStateProvider foliageProvider, BlockStateProvider rootProvider,
                         BlockStateProvider hangingRootProvider, TagKey<Block> rootReplaceable, int minHeight, int maxHeight,
                         IntProvider branchCount, float leafChance, float flareChance, float hangingRootChance) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(Config::trunkProvider),
                BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(Config::foliageProvider),
                BlockStateProvider.CODEC.fieldOf("root_provider").forGetter(Config::rootProvider),
                BlockStateProvider.CODEC.fieldOf("hanging_root_provider").forGetter(Config::hangingRootProvider),
                TagKey.codec(Registries.BLOCK).fieldOf("root_replaceable").forGetter(Config::rootReplaceable),
                Codec.intRange(2, 64).fieldOf("min_height").forGetter(Config::minHeight),
                Codec.intRange(2, 64).fieldOf("max_height").forGetter(Config::maxHeight),
                IntProvider.codec(0, 8).fieldOf("branch_count").forGetter(Config::branchCount),
                Codec.floatRange(0, 1).fieldOf("leaf_chance").forGetter(Config::leafChance),
                Codec.floatRange(0, 1).fieldOf("flare_chance").forGetter(Config::flareChance),
                Codec.floatRange(0, 1).fieldOf("hanging_root_chance").forGetter(Config::hangingRootChance)
        ).apply(instance, Config::new));
    }
}
