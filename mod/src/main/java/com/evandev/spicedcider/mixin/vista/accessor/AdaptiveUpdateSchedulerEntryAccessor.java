package com.evandev.spicedcider.mixin.vista.accessor;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("vista")
@Mixin(targets = "net.mehvahdjukaar.vista.client.AdaptiveUpdateScheduler$Entry", remap = false)
public interface AdaptiveUpdateSchedulerEntryAccessor {
    @Accessor("phase")
    double spicedcider$getPhase();

    @Accessor("phase")
    void spicedcider$setPhase(double phase);
}
