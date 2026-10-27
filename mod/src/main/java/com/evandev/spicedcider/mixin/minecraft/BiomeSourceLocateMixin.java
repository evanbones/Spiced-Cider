package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.locate.BiomeSearch;
import com.evandev.spicedcider.locate.LocateWorkers;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@Mixin(BiomeSource.class)
public class BiomeSourceLocateMixin {

    @WrapMethod(method = "findClosestBiome3d")
    private Pair<BlockPos, Holder<Biome>> spicedcider$parallelSearch(BlockPos pos, int radius, int horizontalStep, int verticalStep, Predicate<Holder<Biome>> biomePredicate,
                                                                   Climate.Sampler sampler, LevelReader level, Operation<Pair<BlockPos, Holder<Biome>>> original) {
        try {
            return BiomeSearch.findClosest((BiomeSource) (Object) this, pos, radius, horizontalStep, verticalStep, biomePredicate, sampler, level);
        } catch (LocateWorkers.LocateFailure failure) {
            LocateWorkers.logFallback(failure.getCause());
            return original.call(pos, radius, horizontalStep, verticalStep, biomePredicate, sampler, level);
        }
    }
}
