package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.vista.accessor.AdaptiveUpdateSchedulerEntryAccessor;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.AdaptiveUpdateScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("vista")
@Mixin(value = AdaptiveUpdateScheduler.class, remap = false)
public abstract class VistaSchedulerPhaseMixin {

    @Shadow
    private static double stablePhaseFromId(Object id) {
        throw new AssertionError();
    }

    @Unique
    private Object spicedcider$currentId;
    @Unique
    private boolean spicedcider$forced;

    @Inject(method = "runIfShouldUpdate(Ljava/lang/Object;DLjava/lang/Runnable;)V", at = @At("HEAD"))
    private void spicedcider$rememberId(Object id, double maxUpdatesPerSecond, Runnable update, CallbackInfo ci) {
        spicedcider$currentId = id;
    }

    @Inject(method = "advancePhaseAndCheckIfDue", at = @At("HEAD"))
    private void spicedcider$noteForced(@Coerce Object entry, double step, CallbackInfoReturnable<Boolean> cir) {
        spicedcider$forced = ((AdaptiveUpdateSchedulerEntryAccessor) entry).spicedcider$getPhase() >= 1.0;
    }

    @Inject(method = "advancePhaseAndCheckIfDue", at = @At("RETURN"))
    private void spicedcider$respreadAfterForced(@Coerce Object entry, double step, CallbackInfoReturnable<Boolean> cir) {
        if (spicedcider$forced && cir.getReturnValueZ() && SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            ((AdaptiveUpdateSchedulerEntryAccessor) entry).spicedcider$setPhase(stablePhaseFromId(spicedcider$currentId));
        }
    }
}
