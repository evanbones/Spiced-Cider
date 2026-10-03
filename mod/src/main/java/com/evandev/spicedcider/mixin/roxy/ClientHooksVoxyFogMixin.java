package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.VoxyFog;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("roxy")
@Mixin(value = ClientHooks.class, remap = false)
public class ClientHooksVoxyFogMixin {

    @WrapOperation(
            method = "onFogRender",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/event/ViewportEvent$RenderFog;isCanceled()Z")
    )
    private static boolean spicedcider$applyVoxyFog(ViewportEvent.RenderFog event, Operation<Boolean> original) {
        return VoxyFog.capture(event, original.call(event));
    }
}
