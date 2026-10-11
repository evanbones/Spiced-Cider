package com.evandev.spicedcider.mixin.nomansland;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@IfModLoaded("nomansland")
@Mixin(value = ChunkGeneratorStructureState.class, priority = 1500)
public class MeetingPointSearchMixin {

    @Unique
    private static final int SPICEDCIDER$QUART_STEP = 8;

    @WrapOperation(
            method = "findBiome",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/BiomeSource;findBiomeHorizontal(IIIILjava/util/function/Predicate;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/biome/Climate$Sampler;)Lcom/mojang/datafixers/util/Pair;"
            )
    )
    private Pair<BlockPos, Holder<Biome>> spicedcider$coarseMeetingPointSearch(BiomeSource source, int x, int y, int z, int radius, Predicate<Holder<Biome>> predicate, RandomSource random, Climate.Sampler sampler, Operation<Pair<BlockPos, Holder<Biome>>> original) {
        return source.findBiomeHorizontal(x, y, z, radius, SPICEDCIDER$QUART_STEP, predicate, random, false, sampler);
    }
}
