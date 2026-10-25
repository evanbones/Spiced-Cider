package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.weather.VisualWeather;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FogRenderer.class)
public class FogRendererOvercastMixin {

    @WrapMethod(method = "setupColor")
    private static void spicedcider$overcastFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks,
                                                     float darkenWorldAmount, Operation<Void> original) {
        VisualWeather.enter();
        try {
            original.call(camera, partialTick, level, renderDistanceChunks, darkenWorldAmount);
        } finally {
            VisualWeather.exit();
        }
    }
}
