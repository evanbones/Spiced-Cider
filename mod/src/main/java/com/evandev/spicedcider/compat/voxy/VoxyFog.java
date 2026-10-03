package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.lang.ref.WeakReference;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class VoxyFog {
    private static final float TRANSITION_SECONDS = 3.0F;

    private static boolean pending;
    private static float fogStart;
    private static float fogEnd;
    private static float underground;
    private static float weather;
    private static long lastNanos;
    private static WeakReference<ClientLevel> lastLevel = new WeakReference<>(null);

    private VoxyFog() {
    }

    public static void prepare(Camera camera, FogRenderer.FogMode mode, float farPlaneDistance, boolean thickFog, float partialTick) {
        if (mode != FogRenderer.FogMode.FOG_TERRAIN) return;
        pending = false;

        SpicedCiderConfig.Client config = SpicedCiderConfig.CLIENT;
        if (!SpicedCiderConfig.CLIENT_SPEC.isLoaded() || !config.voxyFogOverride.get()) return;
        if (thickFog || camera.getFluidInCamera() != FogType.NONE) return;
        if (camera.getEntity() instanceof LivingEntity entity
                && (entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS))) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        float distance = VoxyBridge.lodDistance();
        if (distance <= 0) distance = farPlaneDistance;

        updateEnvironment(level, camera, partialTick);

        float start = distance * config.voxyFogStart.get() / 100.0F;
        float end = distance * config.voxyFogEnd.get() / 100.0F;

        float caveMultiplier = 1.0F - underground * config.voxyCaveFogDensity.get() / 100.0F;
        start *= caveMultiplier;
        end *= caveMultiplier;

        float weatherMultiplier = 1.0F - Mth.clamp(weather - underground, 0.0F, 1.0F) * config.voxyWeatherFogDensity.get() / 100.0F;
        start *= weatherMultiplier;
        end *= weatherMultiplier;

        fogEnd = Math.max(end, 16.0F);
        fogStart = Math.max(0.0F, Math.min(start, fogEnd - 8.0F));
        pending = true;
    }

    public static void finish(FogRenderer.FogMode mode) {
        if (mode != FogRenderer.FogMode.FOG_TERRAIN || !pending) return;
        pending = false;
        RenderSystem.setShaderFogStart(fogStart);
        RenderSystem.setShaderFogEnd(fogEnd);
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!pending || event.getMode() != FogRenderer.FogMode.FOG_TERRAIN) return;
        event.setNearPlaneDistance(fogStart);
        event.setFarPlaneDistance(fogEnd);
        event.setFogShape(FogShape.SPHERE);
    }

    public static boolean capture(ViewportEvent.RenderFog event, boolean canceled) {
        if (!pending || event.getMode() != FogRenderer.FogMode.FOG_TERRAIN) return canceled;
        fogEnd = Math.max(event.getFarPlaneDistance(), 16.0F);
        fogStart = Math.max(0.0F, Math.min(event.getNearPlaneDistance(), fogEnd - 8.0F));
        event.setNearPlaneDistance(fogStart);
        event.setFarPlaneDistance(fogEnd);
        return true;
    }

    private static void updateEnvironment(ClientLevel level, Camera camera, float partialTick) {
        BlockPos pos = camera.getBlockPosition();
        float undergroundTarget = 0.0F;
        float weatherTarget = 0.0F;
        if (level.dimensionType().hasSkyLight()) {
            float sky = level.getBrightness(LightLayer.SKY, pos) / 15.0F;
            float depth = Mth.clamp((level.getSeaLevel() + 32.0F - (float) camera.getPosition().y) / 64.0F, 0.0F, 1.0F);
            undergroundTarget = depth * (1.0F - sky);
            if (level.getBiome(pos).value().hasPrecipitation()) {
                weatherTarget = 0.5F * level.getRainLevel(partialTick) + 0.5F * level.getThunderLevel(partialTick);
            }
        }

        long now = System.nanoTime();
        if (lastLevel.get() != level || lastNanos == 0) {
            lastLevel = new WeakReference<>(level);
            underground = undergroundTarget;
            weather = weatherTarget;
        } else {
            float seconds = Math.min((now - lastNanos) / 1.0E9F, 1.0F);
            float blend = 1.0F - (float) Math.exp(-seconds / TRANSITION_SECONDS);
            underground += (undergroundTarget - underground) * blend;
            weather += (weatherTarget - weather) * blend;
        }
        lastNanos = now;
    }
}
