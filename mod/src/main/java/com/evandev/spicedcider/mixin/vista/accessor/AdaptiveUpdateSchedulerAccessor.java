package com.evandev.spicedcider.mixin.vista.accessor;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.AdaptiveUpdateScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("vista")
@Mixin(value = AdaptiveUpdateScheduler.class, remap = false)
public interface AdaptiveUpdateSchedulerAccessor {

    @Accessor("thisFrameAccumulatedUpdateTimeNano")
    long spicedcider$getAccumulatedUpdateNanos();

    @Accessor("thisFrameAccumulatedUpdateTimeNano")
    void spicedcider$setAccumulatedUpdateNanos(long nanos);
}
