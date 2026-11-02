package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.compat.wherewindsblow.WindStreakSegmentGates;
import com.jvn.wherewindsblow.client.weather.BlizzardWeatherEffects;
import com.jvn.wherewindsblow.client.wind.WindStreakRenderer;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("where_winds_blow")
@Mixin(value = WindStreakRenderer.class, remap = false)
public class WindStreakSegmentGateMixin {

    @Inject(method = "drawSegment", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$beginGate(VertexConsumer consumer, Matrix4f pose, ClientLevel level, @Coerce Object streak, Vec3 cameraPos,
                                              double baseX, double baseY, double baseZ, float t0, float t1, float windTime, float brushPosition,
                                              float alpha, BlizzardWeatherEffects.BlizzardFogProfile blizzardFog, CallbackInfo ci) {
        if (!WindStreakSegmentGates.begin(streak, t0, level.getGameTime())) ci.cancel();
    }

    @Inject(method = "drawSegment", at = @At("RETURN"))
    private static void spicedcider$endGate(CallbackInfo ci) {
        WindStreakSegmentGates.end();
    }

    @WrapMethod(method = "isOpenSkyAir")
    private static boolean spicedcider$cachedOpenSky(ClientLevel level, double x, double y, double z, Operation<Boolean> original) {
        if (WindStreakSegmentGates.bypassing()) return true;
        boolean open = original.call(level, x, y, z);
        WindStreakSegmentGates.record(open);
        return open;
    }

    @WrapMethod(method = "lineHitsCollision(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Z")
    private static boolean spicedcider$cachedCollision(ClientLevel level, Vec3 start, Vec3 end, Operation<Boolean> original) {
        if (WindStreakSegmentGates.bypassing()) return false;
        boolean hit = original.call(level, start, end);
        WindStreakSegmentGates.record(!hit);
        return hit;
    }
}
