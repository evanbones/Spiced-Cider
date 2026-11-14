package com.evandev.spicedcider.wind.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliablegliders.GliderWind;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class ClientWindEvents {
    private static final boolean GLIDERS = ModList.get().isLoaded("reliable_gliders");

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.isPaused()) return;
        ClientWind.tick(level.dimensionType().hasCeiling());
        WindParticles.configure(
                SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.windParticles, true),
                0.05F * SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.windParticleStrength, 100) / 100.0F);
        if (!ClientWind.active()) return;
        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
        WindExposure.refresh(level, Mth.floor(camera.x), Mth.floor(camera.z));
        if (SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.windMotes, true)) WindMotes.tick(minecraft, level, camera);
        if (GLIDERS && minecraft.player != null
                && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.windGliders, true)) GliderWind.tick(minecraft.player);
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        WindParticles.reload();
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        WindParticles.reload();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientWind.reset();
        WindExposure.reset();
    }
}
