package com.evandev.spicedcider.mixin.lithostitched;

import com.evandev.spicedcider.util.MappedDensityCache;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.SampleDensityPlacementCondition;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("lithostitched")
@Mixin(value = SampleDensityPlacementCondition.class, remap = false)
public class SampleDensityPlacementConditionMixin {

    @WrapOperation(
            method = "test(Ldev/worldgen/lithostitched/api/worldgen/placementcondition/PlacementCondition$Context;Lnet/minecraft/core/BlockPos;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/DensityFunction;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/DensityFunction;")
    )
    private DensityFunction spicedcider$reuseMappedDensity(DensityFunction function, DensityFunction.Visitor visitor, Operation<DensityFunction> original, @Local(argsOnly = true) PlacementCondition.Context context) {
        return MappedDensityCache.get(context.randomState(), function, f -> original.call(f, visitor));
    }
}
