package com.evandev.spicedcider.weather.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.weather.CloudCover;
import com.evandev.spicedcider.weather.VisualWeather;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class CloudCoverClient {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "dynamic/cloud_cover");
    private static final ResourceLocation PARTLY_CLOUDY_SOURCE = ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");
    private static final ResourceLocation MOSTLY_CLEAR_SOURCE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/environment/clouds_mostly_clear.png");
    private static final ResourceLocation CLOUDY_SOURCE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/environment/clouds_cloudy.png");
    private static final float OVERCAST_RAIN = 0.8F;
    private static final int FAST_WINDOW_TICKS = 100;

    private static CloudCover received = CloudCover.PARTLY_CLOUDY;
    private static boolean snap = true;
    private static boolean awaitingSync = true;
    private static CloudCover loginCover;

    private static int[][] sources;
    private static int width;
    private static int height;
    private static boolean sourcesFailed;

    private static DynamicTexture texture;
    private static int[] composed;
    private static int[] from;
    private static CloudCover target;
    private static float fromOvercast;
    private static float progress = 1.0F;
    private static float composedAmount = -1.0F;
    private static long version;
    private static int ticks;
    private static int fastUntil;
    private static float transitionTicks = 800.0F;

    public static void receive(CloudCover cover, boolean fast) {
        received = cover;
        if (fast) fastUntil = ticks + FAST_WINDOW_TICKS;
        if (awaitingSync) {
            awaitingSync = false;
            loginCover = cover;
            snap = true;
        }
    }

    public static boolean active() {
        ClientLevel level = Minecraft.getInstance().level;
        return composed != null && level != null && level.dimensionType().hasSkyLight();
    }

    public static ResourceLocation texture(ResourceLocation original) {
        return active() ? TEXTURE : original;
    }

    public static float alphaAt(int x, int z) {
        return (composed[Math.floorMod(z, height) * width + Math.floorMod(x, width)] >>> 24) / 255.0F;
    }

    public static long version() {
        return active() ? version : 0;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || !loadSources(minecraft.getResourceManager())) return;
        ticks++;

        CloudCover wanted = level.getLevelData().isRaining() ? CloudCover.CLOUDY : received;
        if (texture == null) {
            texture = new DynamicTexture(new NativeImage(width, height, false));
            minecraft.getTextureManager().register(TEXTURE, texture);
            composed = new int[width * height];
            from = new int[width * height];
            snap = true;
        }
        if (snap) {
            snap = false;
            target = level.getLevelData().isRaining() || loginCover == null ? wanted : loginCover;
            loginCover = null;
            fromOvercast = target.overcast();
            progress = 1.0F;
            composedAmount = -1.0F;
            compose(1.0F);
        }
        if (wanted != target) {
            System.arraycopy(composed, 0, from, 0, composed.length);
            fromOvercast = overcast();
            target = wanted;
            transitionTicks = fadeSeconds(ticks < fastUntil) * 20.0F;
            progress = transitionTicks <= 0.0F ? 1.0F : 0.0F;
            composedAmount = -1.0F;
        }

        if (!minecraft.isPaused() && progress < 1.0F) {
            progress = Math.min(1.0F, progress + 1.0F / transitionTicks);
        }
        float amount = eased();
        if (amount != composedAmount) {
            composedAmount = amount;
            compose(amount);
        }
        VisualWeather.setOvercastRain(OVERCAST_RAIN * overcast(progress));
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        received = CloudCover.PARTLY_CLOUDY;
        loginCover = null;
        snap = true;
        awaitingSync = true;
        VisualWeather.setOvercastRain(0.0F);
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        snap = true;
        awaitingSync = true;
    }

    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
                sources = null;
                sourcesFailed = false;
                composedAmount = -1.0F;
            }
        });
    }

    private static float fadeSeconds(boolean fast) {
        if (!SpicedCiderConfig.CLIENT_SPEC.isLoaded()) return fast ? 5.0F : 40.0F;
        return fast ? SpicedCiderConfig.CLIENT.cloudCoverCommandFadeSeconds.get() : SpicedCiderConfig.CLIENT.cloudCoverFadeSeconds.get();
    }

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (target == null || minecraft.level == null || progress >= 1.0F || minecraft.isPaused()) return;
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        VisualWeather.setOvercastRain(OVERCAST_RAIN * overcast(Math.min(1.0F, progress + partial / transitionTicks)));
    }

    private static float overcast() {
        return overcast(progress);
    }

    private static float overcast(float at) {
        return Mth.lerp(eased(at), fromOvercast, target.overcast());
    }

    private static float eased() {
        return eased(progress);
    }

    private static float eased(float at) {
        return at * at * (3.0F - 2.0F * at);
    }

    private static void compose(float amount) {
        int[] to = sources[target.ordinal()];
        for (int i = 0; i < composed.length; i++) {
            composed[i] = blend(from[i], to[i], amount);
        }
        NativeImage pixels = texture.getPixels();
        if (pixels == null) return;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels.setPixelRGBA(x, y, composed[y * width + x]);
            }
        }
        texture.upload();
        version++;
    }

    private static int blend(int from, int to, float amount) {
        if (amount <= 0.0F) return from;
        if (amount >= 1.0F) return to;
        int fromAlpha = from >>> 24;
        int toAlpha = to >>> 24;
        int alpha = Math.round(Mth.lerp(amount, fromAlpha, toAlpha));
        int color = fromAlpha == 0 ? to : toAlpha == 0 ? from : amount < 0.5F ? from : to;
        return alpha << 24 | color & 0xFFFFFF;
    }

    private static boolean loadSources(ResourceManager resources) {
        if (sources != null) return true;
        if (sourcesFailed) return false;

        int[] partly = read(resources, PARTLY_CLOUDY_SOURCE, 0, 0);
        if (partly == null) {
            sourcesFailed = true;
            return false;
        }
        int[] mostlyClear = read(resources, MOSTLY_CLEAR_SOURCE, width, height);
        int[] cloudy = read(resources, CLOUDY_SOURCE, width, height);
        int[] clear = new int[partly.length];
        sources = new int[][]{
                clear,
                mostlyClear == null ? clear : mostlyClear,
                partly,
                cloudy == null ? partly : cloudy
        };

        if (composed != null && composed.length != width * height) {
            texture = null;
            composed = null;
        }
        snap = true;
        return true;
    }

    private static int[] read(ResourceManager resources, ResourceLocation location, int targetWidth, int targetHeight) {
        Optional<Resource> resource = resources.getResource(location);
        if (resource.isEmpty()) {
            SpicedCider.LOGGER.warn("Missing cloud texture {}", location);
            return null;
        }
        try (InputStream stream = resource.get().open(); NativeImage image = NativeImage.read(stream)) {
            if (targetWidth == 0) {
                width = image.getWidth();
                height = image.getHeight();
                targetWidth = width;
                targetHeight = height;
            }
            int[] pixels = new int[targetWidth * targetHeight];
            for (int y = 0; y < targetHeight; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    pixels[y * targetWidth + x] = image.getPixelRGBA(x * image.getWidth() / targetWidth, y * image.getHeight() / targetHeight);
                }
            }
            return pixels;
        } catch (IOException e) {
            SpicedCider.LOGGER.error("Failed to read cloud texture {}", location, e);
            return null;
        }
    }
}
