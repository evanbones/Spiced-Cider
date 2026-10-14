package com.evandev.spicedcider.mixin.c2me;

import com.evandev.spicedcider.mixin.c2me.accessor.ArchitecturyEventAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.architectury.event.events.common.ChunkEvent;
import net.neoforged.bus.ListenerList;
import net.neoforged.bus.api.EventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("c2me")
@IfModLoaded("architectury")
@Mixin(targets = "com.ishland.c2me.base.common.util.HookCompatibility", remap = false)
public class HookCompatibilityMixin {

    @WrapOperation(method = "isChunkSaveEventFree", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/ListenerList;getListeners()[Lnet/neoforged/bus/api/EventListener;"))
    private static EventListener[] spicedcider$ignoreIdleArchitecturyForwarder(ListenerList list, Operation<EventListener[]> original) {
        EventListener[] listeners = original.call(list);
        if (listeners != null && listeners.length == 1 && ((ArchitecturyEventAccessor) ChunkEvent.SAVE_DATA).spicedcider$getListeners().isEmpty()) {
            return new EventListener[0];
        }
        return listeners;
    }
}
