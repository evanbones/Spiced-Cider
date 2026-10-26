package com.evandev.spicedcider.lodgen.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.voxy.VoxyBridge;
import com.evandev.spicedcider.compat.voxy.VoxyIngest;
import com.evandev.spicedcider.lodgen.LodColumn;
import com.evandev.spicedcider.lodgen.LodGen;
import com.evandev.spicedcider.networking.LodChunkPayload;
import com.evandev.spicedcider.networking.LodHelloPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class LodClient {
    private static final int CHECK_INTERVAL = 20;
    private static int sentRadius = -1;
    private static int ticks;

    public static void receive(LodChunkPayload payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level.dimension() != payload.dimension()) return;
        LodColumn column = LodColumn.decode(payload.x(), payload.z(), payload.data(), level.registryAccess().registryOrThrow(Registries.BIOME));
        VoxyIngest.ingest(level, column);
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        sentRadius = -1;
        sendRadius();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (++ticks % CHECK_INTERVAL == 0 && Minecraft.getInstance().getConnection() != null) sendRadius();
    }

    private static void sendRadius() {
        int radius = VoxyIngest.available() ? VoxyBridge.lodRadiusChunks() : 0;
        if (radius == sentRadius) return;
        sentRadius = radius;
        PacketDistributor.sendToServer(new LodHelloPayload(radius));
    }

    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.getDebugOverlay().showDebugScreen() || minecraft.getSingleplayerServer() == null) return;
        String line = LodGen.debugLine();
        if (line != null) event.getLeft().add(line);
    }
}
