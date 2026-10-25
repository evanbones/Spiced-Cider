package com.evandev.spicedcider.weather.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.livingwind.LivingWindBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class CloudWind {
    private static final boolean LIVING_WIND = ModList.get().isLoaded("livingwind");
    private static final double BLOCKS_PER_STRENGTH = 0.1;
    private static final double MIN_SPEED = 0.0075;

    private static boolean active;
    private static double prevX;
    private static double prevZ;
    private static double x;
    private static double z;

    public static boolean active() {
        return active;
    }

    public static double travelX() {
        return Mth.lerp(partialTick(), prevX, x);
    }

    public static double travelZ() {
        return Mth.lerp(partialTick(), prevZ, z);
    }

    private static float partialTick() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!LIVING_WIND || minecraft.level == null || minecraft.isPaused()) return;
        if (!active) {
            if (!LivingWindBridge.hasWind()) return;
            active = true;
        }
        prevX = x;
        prevZ = z;
        float strength = LivingWindBridge.strength();
        double speed = Math.max(MIN_SPEED, strength * BLOCKS_PER_STRENGTH);
        double dirX = strength > 1.0E-4F ? LivingWindBridge.windX() / strength : 1.0;
        double dirZ = strength > 1.0E-4F ? LivingWindBridge.windZ() / strength : 0.0;
        x -= dirX * speed;
        z -= dirZ * speed;
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        active = false;
        prevX = prevZ = x = z = 0.0;
    }
}
