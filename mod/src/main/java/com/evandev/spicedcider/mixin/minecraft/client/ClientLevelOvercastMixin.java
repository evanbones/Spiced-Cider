package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.weather.VisualWeather;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public class ClientLevelOvercastMixin {

    @WrapMethod(method = "getSkyDarken")
    private float spicedcider$overcastSkyDarken(float partialTick, Operation<Float> original) {
        VisualWeather.enter();
        try {
            return original.call(partialTick);
        } finally {
            VisualWeather.exit();
        }
    }

    @WrapMethod(method = "getSkyColor")
    private Vec3 spicedcider$overcastSkyColor(Vec3 pos, float partialTick, Operation<Vec3> original) {
        VisualWeather.enter();
        try {
            return original.call(pos, partialTick);
        } finally {
            VisualWeather.exit();
        }
    }

    @WrapMethod(method = "getCloudColor")
    private Vec3 spicedcider$overcastCloudColor(float partialTick, Operation<Vec3> original) {
        VisualWeather.enter();
        try {
            return original.call(partialTick);
        } finally {
            VisualWeather.exit();
        }
    }
}
