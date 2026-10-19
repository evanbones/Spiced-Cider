package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.worldgen.PaleGarden;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.lang.ref.WeakReference;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class PaleGardenFog {
    private static final float FOG_START = 0.0F;
    private static final float FOG_END = 22.0F;
    private static final float TRANSITION_SECONDS = 1.25F;

    private static float weight;
    private static long lastNanos;
    private static WeakReference<ClientLevel> lastLevel = new WeakReference<>(null);
    private static float fogStart = Float.MAX_VALUE;
    private static float fogEnd = Float.MAX_VALUE;

    public static float fogStart() {
        return fogStart;
    }

    public static float fogEnd() {
        return fogEnd;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || event.getType() != FogType.NONE) return;

        float target = PaleGarden.contains(level, event.getCamera().getBlockPosition()) ? 1.0F : 0.0F;
        long now = System.nanoTime();
        if (lastLevel.get() != level || lastNanos == 0) {
            lastLevel = new WeakReference<>(level);
            weight = target;
        } else {
            float seconds = Math.min((now - lastNanos) / 1.0E9F, 1.0F);
            weight += (target - weight) * (1.0F - (float) Math.exp(-seconds / (TRANSITION_SECONDS / 3.0F)));
        }
        lastNanos = now;
        if (weight < 0.001F) {
            fogStart = Float.MAX_VALUE;
            fogEnd = Float.MAX_VALUE;
            return;
        }

        float baseFar = Math.max(event.getFarPlaneDistance(), FOG_END);
        float far = 1.0F / Mth.lerp(weight, 1.0F / baseFar, 1.0F / FOG_END);
        float startRatio = Mth.lerp(weight, event.getNearPlaneDistance() / baseFar, FOG_START / FOG_END);
        event.setNearPlaneDistance(far * startRatio);
        event.setFarPlaneDistance(far);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
        fogStart = far * startRatio;
        fogEnd = far;
    }
}
