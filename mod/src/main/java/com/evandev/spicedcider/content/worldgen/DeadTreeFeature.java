package com.evandev.spicedcider.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DeadTreeFeature extends Feature<DeadTreeFeature.Config> {
    private static final int MAX_BRANCHES_PER_LEVEL = 2;

    public DeadTreeFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        if (!level.getBlockState(origin.below()).is(config.ground())) return false;
        int height = config.height().sample(random);
        for (int i = 0; i < height; i++) {
            if (!isFree(level, origin.above(i))) return false;
        }

        for (int i = 0; i < height; i++) {
            BlockPos pos = origin.above(i);
            setBlock(level, pos, axis(config.trunk().getState(random, pos)));
        }

        List<Direction> directions = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
        Set<Direction> below = EnumSet.noneOf(Direction.class);
        int placed = 0;
        for (int i = 1; i < height; i++) {
            BlockPos trunk = origin.above(i);
            Set<Direction> here = EnumSet.noneOf(Direction.class);
            Util.shuffle(directions, random);
            for (Direction direction : directions) {
                if (here.size() >= MAX_BRANCHES_PER_LEVEL) break;
                if (below.contains(direction) || random.nextFloat() >= config.branchChance()) continue;
                if (placeBranch(level, random, config, trunk, direction)) {
                    here.add(direction);
                    placed++;
                }
            }
            below = here;
        }
        if (placed == 0 && height > 1) {
            BlockPos trunk = origin.above(1 + random.nextInt(height - 1));
            placeBranch(level, random, config, trunk, Direction.Plane.HORIZONTAL.getRandomDirection(random));
        }
        return true;
    }

    private boolean placeBranch(WorldGenLevel level, RandomSource random, Config config, BlockPos trunk, Direction direction) {
        BlockPos pos = trunk.relative(direction);
        if (!isFree(level, pos)) return false;
        BlockState state = config.branch().getState(random, pos);
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, direction);
        }
        setBlock(level, pos, state);
        return true;
    }

    private static BlockState axis(BlockState state) {
        return state.hasProperty(BlockStateProperties.AXIS) ? state.setValue(BlockStateProperties.AXIS, Direction.Axis.Y) : state;
    }

    private static boolean isFree(WorldGenLevel level, BlockPos pos) {
        return level.isStateAtPosition(pos, state -> state.isAir() || (state.canBeReplaced() && state.getFluidState().isEmpty()));
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider branch, IntProvider height, float branchChance,
                         HolderSet<Block> ground) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("branch").forGetter(Config::branch),
                IntProvider.codec(1, 16).fieldOf("height").forGetter(Config::height),
                Codec.floatRange(0, 1).fieldOf("branch_chance").forGetter(Config::branchChance),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("ground").forGetter(Config::ground)
        ).apply(instance, Config::new));
    }
}
