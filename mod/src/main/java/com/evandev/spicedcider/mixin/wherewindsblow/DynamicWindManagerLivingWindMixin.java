package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.compat.livingwind.LivingWindBridge;
import com.jvn.wherewindsblow.client.wind.DynamicWindManager;
import com.jvn.wherewindsblow.client.wind.GlobalWindState;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("where_winds_blow")
@IfModLoaded("livingwind")
@Mixin(value = DynamicWindManager.class, remap = false)
public class DynamicWindManagerLivingWindMixin {

    @Shadow
    private static GlobalWindState state;

    @Shadow
    private static float ambientStrength;

    @Shadow
    private static float effectiveBaseStrengthMultiplier;

    @Inject(method = "update", at = @At("TAIL"))
    private static void spicedcider$useLivingWind(CallbackInfo ci) {
        if (!LivingWindBridge.hasWind()) return;
        float strength = LivingWindBridge.strength();
        float x = state.directionX();
        float z = state.directionZ();
        if (strength > 1.0E-4F) {
            x = LivingWindBridge.windX() / strength;
            z = LivingWindBridge.windZ() / strength;
        }
        ambientStrength = strength;
        float profiled = strength * effectiveBaseStrengthMultiplier;
        state = new GlobalWindState(x, z, x, z, x, z, profiled, profiled, profiled, 0.0F,
                state.ambientTurbulence(), state.ambientTurbulence(), 0.0F, state.weatherPower(),
                state.rainLevel(), state.thunderLevel(), 0.0F, 1.0F, state.profileId());
    }
}
