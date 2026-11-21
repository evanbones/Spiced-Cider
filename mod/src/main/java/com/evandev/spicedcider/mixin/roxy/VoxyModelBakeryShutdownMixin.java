package com.evandev.spicedcider.mixin.roxy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.locks.LockSupport;

@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.model.ModelBakerySubsystem", remap = false)
public class VoxyModelBakeryShutdownMixin {

    @WrapOperation(method = "shutdown", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;join()V"))
    private void spicedcider$joinWithoutLostWakeup(Thread thread, Operation<Void> original) throws InterruptedException {
        while (thread.isAlive()) {
            LockSupport.unpark(thread);
            thread.join(50L);
        }
    }
}
