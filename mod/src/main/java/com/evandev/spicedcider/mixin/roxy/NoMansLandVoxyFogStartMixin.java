package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.VoxyFog;
import com.farcr.nomansland.client.ambience.FogModifierHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("roxy")
@IfModLoaded("nomansland")
@Mixin(value = FogModifierHandler.class, remap = false)
public class NoMansLandVoxyFogStartMixin {

    @WrapOperation(
            method = "renderFog",
            at = @At(value = "INVOKE", target = "Lcom/farcr/nomansland/client/ambience/FogModifierHandler;getFogStartMultiplier(F)F")
    )
    private static float spicedcider$keepVoxyFogStart(FogModifierHandler handler, float partialTicks, Operation<Float> original,
                                                      @Local(argsOnly = true) ViewportEvent.RenderFog event) {
        return VoxyFog.controlsTerrainFog(event) ? 1.0F : original.call(handler, partialTicks);
    }
}
