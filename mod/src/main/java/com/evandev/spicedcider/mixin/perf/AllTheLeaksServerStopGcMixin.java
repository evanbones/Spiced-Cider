package com.evandev.spicedcider.mixin.perf;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("alltheleaks")
@Mixin(targets = "dev.uncandango.alltheleaks.events.CommonEvents", remap = false)
public class AllTheLeaksServerStopGcMixin {
    @WrapOperation(method = "onClosedServer", at = @At(value = "INVOKE", target = "Ldev/uncandango/alltheleaks/feature/common/mods/minecraft/MemoryMonitor;runExplicitGc()Z"))
    private static boolean spicedcider$gcOffThread(Operation<Boolean> original) {
        Thread.ofPlatform().daemon().name("Spiced Cider server-stop GC").start(original::call);
        return true;
    }
}
