package com.evandev.spicedcider.mixin.quark;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.client.module.UsageTickerModule;

@IfModLoaded("quark")
@Mixin(UsageTickerModule.Client.class)
public class UsageTickerPartialTickMixin {

    // gets rid of that horrific jitter
    @WrapOperation(
            method = "renderHUD",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaTicks()F"
            ),
            remap = false
    )
    private float spicedcider$useRealPartialTick(DeltaTracker deltaTracker, Operation<Float> original) {
        return deltaTracker.getGameTimeDeltaPartialTick(false);
    }
}
