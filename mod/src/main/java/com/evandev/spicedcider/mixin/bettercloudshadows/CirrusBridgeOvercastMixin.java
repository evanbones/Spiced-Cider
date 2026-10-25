package com.evandev.spicedcider.mixin.bettercloudshadows;

import com.evandev.spicedcider.weather.VisualWeather;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("better_cloud_shadows")
@Mixin(targets = "com.evandev.better_cloud_shadows.compat.cirrus.CirrusBridge", remap = false)
public class CirrusBridgeOvercastMixin {

    @ModifyExpressionValue(method = "field", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"))
    private static float spicedcider$overcastRain(float rain) {
        return VisualWeather.overcast(rain);
    }
}
