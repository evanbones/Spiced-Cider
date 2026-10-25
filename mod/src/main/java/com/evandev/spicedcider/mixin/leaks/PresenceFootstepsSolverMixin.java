package com.evandev.spicedcider.mixin.leaks;

import com.evandev.spicedcider.util.ClientCacheRegistry;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("presencefootsteps")
@Mixin(targets = "eu.ha3.presencefootsteps.world.PFSolver", remap = false)
public class PresenceFootstepsSolverMixin {
    @Shadow
    @Final
    private Long2ObjectOpenHashMap<?> associationCache;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$track(CallbackInfo ci) {
        ClientCacheRegistry.track(associationCache);
    }
}
