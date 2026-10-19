package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.registry.ModTreeFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.HashSet;
import java.util.Set;

public class TrunkShootsDecorator extends TreeDecorator {
    public static final MapCodec<TrunkShootsDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter(decorator -> decorator.foliageProvider),
            Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(decorator -> decorator.probability),
            Codec.intRange(0, 16).fieldOf("skip_bottom").forGetter(decorator -> decorator.skipBottom)
    ).apply(instance, TrunkShootsDecorator::new));

    private final BlockStateProvider foliageProvider;
    private final float probability;
    private final int skipBottom;

    public TrunkShootsDecorator(BlockStateProvider foliageProvider, float probability, int skipBottom) {
        this.foliageProvider = foliageProvider;
        this.probability = probability;
        this.skipBottom = skipBottom;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeFeatures.TRUNK_SHOOTS.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        Set<BlockPos> logs = new HashSet<>(context.logs());
        int base = logs.stream().mapToInt(BlockPos::getY).min().orElse(0);
        Set<BlockPos> placed = new HashSet<>();

        for (BlockPos log : context.logs()) {
            if (log.getY() < base + this.skipBottom || random.nextFloat() >= this.probability) continue;
            int attempts = 1 + random.nextInt(4);
            for (int i = 0; i < attempts; i++) {
                BlockPos pos = log.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));
                boolean adjacent = true;
                if (random.nextInt(3) == 0) {
                    pos = pos.relative(random.nextBoolean() ? Direction.UP : Direction.DOWN);
                    adjacent = touches(pos, logs);
                    if (!adjacent && !touches(pos, placed)) continue;
                }
                if (!context.isAir(pos)) continue;
                context.setBlock(pos, this.foliageProvider.getState(random, pos).trySetValue(LeavesBlock.DISTANCE, adjacent ? 1 : 2));
                if (adjacent) placed.add(pos);
            }
        }
    }

    private static boolean touches(BlockPos pos, Set<BlockPos> blocks) {
        for (Direction direction : Direction.values()) {
            if (blocks.contains(pos.relative(direction))) return true;
        }
        return false;
    }
}
