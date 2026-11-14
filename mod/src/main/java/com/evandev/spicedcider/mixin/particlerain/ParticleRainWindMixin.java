package com.evandev.spicedcider.mixin.particlerain;

import com.evandev.spicedcider.wind.client.ClientWind;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@IfModLoaded("particlerain")
@Mixin(targets = "pigcart.particlerain.ParticleRain", remap = false)
public class ParticleRainWindMixin {
    @Unique
    private static final float WIND_PER_STRENGTH = 0.03F;

    @WrapMethod(method = "getWind(DDD)Lorg/joml/Vector3f;")
    private static Vector3f spicedcider$wind(double x, double y, double z, Operation<Vector3f> original) {
        if (!ClientWind.active()) return original.call(x, y, z);
        float scale = ClientWind.exposedStrength(x, y, z) * WIND_PER_STRENGTH;
        return new Vector3f(ClientWind.directionX() * scale, 0.0F, ClientWind.directionZ() * scale);
    }
}
