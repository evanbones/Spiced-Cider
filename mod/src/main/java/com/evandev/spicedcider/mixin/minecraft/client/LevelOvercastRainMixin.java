package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.weather.VisualWeather;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public class LevelOvercastRainMixin {
    @Shadow
    @Final
    public boolean isClientSide;

    @ModifyReturnValue(method = "getRainLevel", at = @At("RETURN"))
    private float spicedcider$overcastRain(float rain) {
        return isClientSide ? VisualWeather.rainLevel(rain) : rain;
    }
}
