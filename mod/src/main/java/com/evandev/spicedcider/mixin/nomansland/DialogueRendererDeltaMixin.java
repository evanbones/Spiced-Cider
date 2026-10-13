package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.client.renderer.DialogueRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = DialogueRenderer.class, remap = false)
public class DialogueRendererDeltaMixin {

    @Unique
    private static final long SPICEDCIDER$MAX_GAP_MS = 250L;

    @Unique
    private static long spicedcider$lastRenderMs = -1L;

    // Fixes friend moon dialogue with gnetum
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DeltaTracker;getGameTimeDeltaTicks()F"))
    private static float spicedcider$deltaSinceLastRender(DeltaTracker tracker, Operation<Float> original) {
        float frameDelta = original.call(tracker);
        long now = Util.getMillis();
        long last = spicedcider$lastRenderMs;
        spicedcider$lastRenderMs = now;
        if (last < 0L || now - last > SPICEDCIDER$MAX_GAP_MS) return frameDelta;
        return Math.max(frameDelta, (now - last) / spicedcider$millisPerTick());
    }

    @Unique
    private static float spicedcider$millisPerTick() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            TickRateManager tickRate = level.tickRateManager();
            if (tickRate.runsNormally()) return Math.max(50.0F, tickRate.millisecondsPerTick());
        }
        return 50.0F;
    }
}
