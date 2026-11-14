package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.wind.client.ClientWind;
import com.jvn.wherewindsblow.client.wind.DynamicWindManager;
import com.jvn.wherewindsblow.client.wind.GlobalWindState;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("where_winds_blow")
@Mixin(value = DynamicWindManager.class, remap = false)
public class DynamicWindManagerWindMixin {

    @Shadow
    private static GlobalWindState state;

    @Shadow
    private static float ambientStrength;

    @Shadow
    private static float effectiveBaseStrengthMultiplier;

    @Inject(method = "update", at = @At("TAIL"))
    private static void spicedcider$useSyncedWind(CallbackInfo ci) {
        if (!ClientWind.active()) return;
        float strength = ClientWind.strength();
        float x = ClientWind.directionX();
        float z = ClientWind.directionZ();
        float turbulence = Math.max(state.ambientTurbulence(), ClientWind.turbulence());
        ambientStrength = strength;
        float profiled = strength * effectiveBaseStrengthMultiplier;
        state = new GlobalWindState(x, z, x, z, x, z, profiled, profiled, profiled, 0.0F,
                turbulence, state.ambientTurbulence(), 0.0F, state.weatherPower(),
                state.rainLevel(), state.thunderLevel(), 0.0F, 1.0F, state.profileId());
    }
}
