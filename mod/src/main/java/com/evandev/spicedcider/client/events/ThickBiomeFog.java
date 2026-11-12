package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModTags;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.lang.ref.WeakReference;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ThickBiomeFog {
    private static final float FOG_START = 20.0F;
    private static final float FOG_END = 56.0F;
    private static final float TRANSITION_SECONDS = 2.0F;
    private static final float DARKEST = 0.16F;
    private static final float BLOCK_LIGHT_SHARE = 0.4F;
    private static final float BRIGHTEST = 0.5F;
    private static final float GLOWING = 0.45F;
    private static final float SKY_BLEND_CUTOFF = 0.15F;
    private static final float SKY_HIDDEN_CUTOFF = 0.5F;

    private static float weight;
    private static float ambient = 1.0F;
    private static float glow;
    private static long lastNanos;
    private static WeakReference<ClientLevel> lastLevel = new WeakReference<>(null);

    public static boolean replacesSkyBlend() {
        return weight > SKY_BLEND_CUTOFF;
    }

    public static boolean hidesSky() {
        return weight > SKY_HIDDEN_CUTOFF;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || event.getType() != FogType.NONE) return;

        BlockPos pos = event.getCamera().getBlockPosition();
        float sky = level.getBrightness(LightLayer.SKY, pos) / 15.0F;
        Holder<Biome> biome = level.getBiome(pos);
        float target = biome.is(ModTags.Biomes.HAS_THICK_FOG) ? 1.0F - sky : 0.0F;
        float glowTarget = biome.is(ModTags.Biomes.HAS_GLOWING_FOG) ? 1.0F : 0.0F;
        float light = Math.max(level.getBrightness(LightLayer.BLOCK, pos) / 15.0F * BLOCK_LIGHT_SHARE, sky);
        long now = System.nanoTime();
        if (lastLevel.get() != level || lastNanos == 0) {
            lastLevel = new WeakReference<>(level);
            weight = target;
            ambient = light;
            glow = glowTarget;
        } else {
            float seconds = Math.min((now - lastNanos) / 1.0E9F, 1.0F);
            float blend = 1.0F - (float) Math.exp(-seconds / (TRANSITION_SECONDS / 3.0F));
            weight += (target - weight) * blend;
            ambient += (light - ambient) * blend;
            glow += (glowTarget - glow) * blend;
        }
        lastNanos = now;
        if (weight < 0.001F) return;

        float baseFar = Math.max(event.getFarPlaneDistance(), FOG_END);
        float far = 1.0F / Mth.lerp(weight, 1.0F / baseFar, 1.0F / FOG_END);
        float startRatio = Mth.lerp(weight, Math.min(event.getNearPlaneDistance(), baseFar) / baseFar, FOG_START / FOG_END);
        event.setNearPlaneDistance(far * startRatio);
        event.setFarPlaneDistance(far);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    public static float colourScale() {
        if (weight < 0.001F) return 1.0F;
        float lit = Mth.lerp(ambient, DARKEST, BRIGHTEST);
        return Mth.lerp(weight, 1.0F, Math.max(lit, glow * GLOWING));
    }
}
