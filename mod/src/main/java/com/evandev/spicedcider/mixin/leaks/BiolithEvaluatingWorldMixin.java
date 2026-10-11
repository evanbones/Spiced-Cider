package com.evandev.spicedcider.mixin.leaks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.terraformersmc.biolith.api.biome.BiolithFittestNodes;
import com.terraformersmc.biolith.impl.biome.DimensionBiomePlacement;
import com.terraformersmc.biolith.impl.commands.BiolithDescribeCommand;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@IfModLoaded("biolith")
@Mixin(value = DimensionBiomePlacement.class, remap = false)
public class BiolithEvaluatingWorldMixin {
    @Final
    @Shadow
    protected static ThreadLocal<?> EVALUATING_WORLD;

    @WrapMethod(method = "getReplacement")
    private Holder<Biome> spicedcider$releaseAfterReplacement(int x, int y, int z, Climate.TargetPoint noisePoint, BiolithFittestNodes<Holder<Biome>> fittestNodes, Operation<Holder<Biome>> original) {
        try {
            return original.call(x, y, z, noisePoint, fittestNodes);
        } finally {
            EVALUATING_WORLD.remove();
        }
    }

    @WrapMethod(method = "getBiomeData")
    private BiolithDescribeCommand.DescribeBiomeData spicedcider$releaseAfterDescribe(int x, int y, int z, Climate.TargetPoint noisePoint, BiolithFittestNodes<Holder<Biome>> fittestNodes, Operation<BiolithDescribeCommand.DescribeBiomeData> original) {
        try {
            return original.call(x, y, z, noisePoint, fittestNodes);
        } finally {
            EVALUATING_WORLD.remove();
        }
    }
}
