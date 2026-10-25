package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.lambdaurora.lambdynlights.engine.lookup.SpatialLookupEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@IfModLoaded("lambdynlights_runtime")
@Mixin(targets = "dev.lambdaurora.lambdynlights.engine.DynamicLightingEngine", remap = false)
public class LambDynLightsEngineMixin {
    @Shadow
    private SpatialLookupEntry[] spatialLookupEntries;

    @Inject(method = "resetSize", at = @At("TAIL"))
    private void spicedcider$dropStaleEntries(CallbackInfo ci) {
        Arrays.fill(spatialLookupEntries, null);
    }
}
