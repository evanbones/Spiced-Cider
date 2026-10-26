package com.evandev.spicedcider.mixin.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("sodium")
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder", remap = false)
public abstract class RenderDistanceMinimumMixin {
    @WrapOperation(method = "buildGeneralPage", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;setRange(III)Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;", ordinal = 0))
    private IntegerOptionBuilder spicedcider$raiseMinimum(IntegerOptionBuilder builder, int min, int max, int step, Operation<IntegerOptionBuilder> original) {
        return original.call(builder, Math.max(min, 3), max, step);
    }
}
