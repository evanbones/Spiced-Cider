package com.evandev.spicedcider.mixin.polytone;

import com.evandev.spicedcider.weather.VisualWeather;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("polytone")
@Mixin(targets = "net.mehvahdjukaar.polytone.common.ClientFrameTicker", remap = false)
public class ClientFrameTickerOvercastMixin {

    @ModifyExpressionValue(method = "onRenderTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getRainLevel(F)F"))
    private static float spicedcider$overcastRain(float rain) {
        return VisualWeather.overcast(rain);
    }
}
