package com.evandev.spicedcider.util;

import com.google.common.collect.MapMaker;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.concurrent.ConcurrentMap;
import java.util.function.UnaryOperator;

public final class MappedDensityCache {
    private static final ConcurrentMap<RandomState, ConcurrentMap<DensityFunction, DensityFunction>> MAPPED = new MapMaker().weakKeys().makeMap();

    public static DensityFunction get(RandomState state, DensityFunction function, UnaryOperator<DensityFunction> mapper) {
        ConcurrentMap<DensityFunction, DensityFunction> perState = MAPPED.computeIfAbsent(state, s -> new MapMaker().weakKeys().makeMap());
        DensityFunction mapped = perState.get(function);
        if (mapped != null) return mapped;
        mapped = mapper.apply(function);
        DensityFunction raced = perState.putIfAbsent(function, mapped);
        return raced != null ? raced : mapped;
    }
}
