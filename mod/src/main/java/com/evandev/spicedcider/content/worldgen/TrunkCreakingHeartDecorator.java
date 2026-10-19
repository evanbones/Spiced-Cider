package com.evandev.spicedcider.content.worldgen;

import com.evandev.spicedcider.registry.ModTreeFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;

public class TrunkCreakingHeartDecorator extends TreeDecorator {
    public static final MapCodec<TrunkCreakingHeartDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability")
            .xmap(TrunkCreakingHeartDecorator::new, decorator -> decorator.probability);
    private static final ResourceLocation CREAKING_HEART = ResourceLocation.withDefaultNamespace("creaking_heart");

    private final float probability;

    public TrunkCreakingHeartDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeFeatures.TRUNK_CREAKING_HEART.get();
    }

    @Override
    public void place(Context context) {
        if (context.random().nextFloat() >= this.probability) return;

        Block heart = BuiltInRegistries.BLOCK.get(CREAKING_HEART);
        if (heart == Blocks.AIR) return;

        int base = context.logs().stream().mapToInt(BlockPos::getY).min().orElse(0);
        List<BlockPos> candidates = context.logs().stream()
                .filter(pos -> pos.getY() >= base + 2)
                .filter(pos -> context.logs().contains(pos.above()) && context.logs().contains(pos.below()))
                .toList();
        if (candidates.isEmpty()) return;

        BlockState state = heart.defaultBlockState();
        state = withValue(state, "creaking_heart_state", "dormant");
        state = withValue(state, "natural", "true");
        context.setBlock(candidates.get(context.random().nextInt(candidates.size())), state);
    }

    private static <T extends Comparable<T>> BlockState withValue(BlockState state, String name, String value) {
        @SuppressWarnings("unchecked")
        Property<T> property = (Property<T>) state.getBlock().getStateDefinition().getProperty(name);
        if (property == null) return state;
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }
}
