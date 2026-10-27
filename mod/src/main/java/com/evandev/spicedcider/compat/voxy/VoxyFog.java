package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.weather.VisualWeather;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.Tags;

import java.lang.ref.WeakReference;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class VoxyFog {
    private static final float TRANSITION_SECONDS = 3.0F;
    private static final float EDGE_CLOSE_SECONDS = 1.5F;
    private static final float EDGE_OPEN_SECONDS = 3.0F;
    private static final float EDGE_MARGIN = 8.0F;
    private static final float DUST_TINT = 0.9F;
    private static final float DUST_FOG_END = 96.0F;
    private static final float DUST_FOG_START = 4.0F;
    private static final float DUST_GLOW = 1.3F;
    private static final float HAZE_LEAD = 0.25F;

    private static boolean pending;
    private static float fogStart;
    private static float fogEnd;
    private static boolean haze;
    private static float underground;
    private static float weather;
    private static float dust;
    private static float redSand;
    private static float edgeTarget = -1.0F;
    private static float edge = -1.0F;
    private static long lastNanos;
    private static WeakReference<ClientLevel> lastLevel = new WeakReference<>(null);

    public static boolean overridesFog() {
        return SpicedCiderConfig.CLIENT_SPEC.isLoaded() && SpicedCiderConfig.CLIENT.voxyFogOverride.get();
    }

    public static void receiveEdge(float distance) {
        edgeTarget = distance;
    }

    public static void prepare(Camera camera, FogRenderer.FogMode mode, float farPlaneDistance, boolean thickFog, float partialTick) {
        if (mode != FogRenderer.FogMode.FOG_TERRAIN) return;
        pending = false;

        SpicedCiderConfig.Client config = SpicedCiderConfig.CLIENT;
        if (!SpicedCiderConfig.CLIENT_SPEC.isLoaded() || !config.voxyFogOverride.get()) return;
        if (camera.getFluidInCamera() != FogType.NONE) return;
        if (camera.getEntity() instanceof LivingEntity entity
                && (entity.hasEffect(MobEffects.BLINDNESS) || entity.hasEffect(MobEffects.DARKNESS))) return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;
        if (thickFog && minecraft.gui.getBossOverlay().shouldCreateWorldFog()) return;

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

        if (thickFog) {
            float hazeMultiplier = 1.0F - config.voxyNetherFogDensity.get() / 100.0F;
            start *= hazeMultiplier;
            end *= hazeMultiplier;
        }

        if (dust > 0.0F) {
            end = Mth.lerp(dust, end, Math.min(end, DUST_FOG_END));
            start = Mth.lerp(dust, start, Math.min(start, DUST_FOG_START));
        }

        if (edge >= 0.0F) {
            float limited = Math.min(end, edge - EDGE_MARGIN);
            if (limited < end) {
                start *= Math.max(limited, 0.0F) / end;
                end = limited;
            }
        }

        fogEnd = Math.max(end, 16.0F);
        haze = thickFog;
        fogStart = Math.max(-fogEnd * 2.0F, Math.min(start, fogEnd - 8.0F));
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (dust < 0.01F || !SpicedCiderConfig.CLIENT_SPEC.isLoaded() || !SpicedCiderConfig.CLIENT.voxyFogOverride.get()) return;
        if (event.getCamera().getFluidInCamera() != FogType.NONE) return;
        float red = event.getRed();
        float green = event.getGreen();
        float blue = event.getBlue();
        float tintRed = Mth.lerp(redSand, 0.92F, 0.88F);
        float tintGreen = Mth.lerp(redSand, 0.62F, 0.42F);
        float tintBlue = Mth.lerp(redSand, 0.32F, 0.20F);
        float scale = DUST_GLOW * luminance(red, green, blue) / luminance(tintRed, tintGreen, tintBlue);
        float blend = dust * DUST_TINT;
        event.setRed(Mth.lerp(blend, red, tintRed * scale));
        event.setGreen(Mth.lerp(blend, green, tintGreen * scale));
        event.setBlue(Mth.lerp(blend, blue, tintBlue * scale));
    }

    private static float luminance(float red, float green, float blue) {
        return 0.2126F * red + 0.7152F * green + 0.0722F * blue;
    }

    public static boolean capture(ViewportEvent.RenderFog event, boolean canceled) {
        if (!pending || event.getMode() != FogRenderer.FogMode.FOG_TERRAIN) return canceled;
        fogEnd = Math.max(event.getFarPlaneDistance(), 16.0F);
        fogStart = clampStart(event.getNearPlaneDistance());
        event.setNearPlaneDistance(fogStart);
        event.setFarPlaneDistance(fogEnd);
        return true;
    }

    private static float clampStart(float start) {
        float clamped = Math.min(start, fogEnd - 8.0F);
        float lead = haze ? fogEnd * HAZE_LEAD : 0.0F;
        return Math.max(-fogEnd * 2.0F, clamped - lead);
    }

    private static void updateEdge(float seconds) {
        if (edgeTarget < 0.0F) {
            edge = edge < 0.0F ? -1.0F : edge + seconds * Math.max(edge, 64.0F) / EDGE_OPEN_SECONDS;
            if (edge > VoxyBridge.lodDistance() * 2.0F) edge = -1.0F;
            return;
        }
        if (edge < 0.0F) edge = Math.max(edgeTarget, VoxyBridge.lodDistance());
        float time = edgeTarget < edge ? EDGE_CLOSE_SECONDS : EDGE_OPEN_SECONDS;
        edge += (edgeTarget - edge) * (1.0F - (float) Math.exp(-seconds / time));
    }

    private static void updateEnvironment(ClientLevel level, Camera camera, float partialTick) {
        BlockPos pos = camera.getBlockPosition();
        float undergroundTarget = 0.0F;
        float weatherTarget = 0.0F;
        float dustTarget = 0.0F;
        float redTarget = redSand;
        if (level.dimensionType().hasSkyLight()) {
            float sky = level.getBrightness(LightLayer.SKY, pos) / 15.0F;
            float depth = Mth.clamp((level.getSeaLevel() + 32.0F - (float) camera.getPosition().y) / 64.0F, 0.0F, 1.0F);
            undergroundTarget = depth * (1.0F - sky);
            if (level.getBiome(pos).value().hasPrecipitation()) {
                weatherTarget = 0.5F * level.getRainLevel(partialTick) + 0.5F * level.getThunderLevel(partialTick);
            }
            weatherTarget = Math.max(weatherTarget, 0.5F * VisualWeather.overcastRain());
            Holder<Biome> biome = level.getBiome(pos);
            boolean badlands = biome.is(Tags.Biomes.IS_BADLANDS);
            if (badlands || biome.is(Tags.Biomes.IS_DESERT)) {
                dustTarget = 0.5F * level.getRainLevel(partialTick) + 0.5F * level.getThunderLevel(partialTick);
                redTarget = badlands ? 1.0F : 0.0F;
                weatherTarget = Math.max(weatherTarget, dustTarget);
            }
        }

        long now = System.nanoTime();
        if (lastLevel.get() != level || lastNanos == 0) {
            if (lastLevel.get() != level) edgeTarget = -1.0F;
            lastLevel = new WeakReference<>(level);
            underground = undergroundTarget;
            weather = weatherTarget;
            dust = dustTarget;
            redSand = redTarget;
            edge = edgeTarget;
        } else {
            float seconds = Math.min((now - lastNanos) / 1.0E9F, 1.0F);
            float blend = 1.0F - (float) Math.exp(-seconds / TRANSITION_SECONDS);
            underground += (undergroundTarget - underground) * blend;
            weather += (weatherTarget - weather) * blend;
            dust += (dustTarget - dust) * blend;
            redSand += (redTarget - redSand) * blend;
            updateEdge(seconds);
        }
        lastNanos = now;
    }
}
