package com.evandev.spicedcider.compat.connectiblechains.client;

import com.evandev.spicedcider.SpicedCider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class ChainClientEvents {
    private static final boolean CHAIN_LIGHTS = ModList.get().isLoaded("connectiblechains") && ModList.get().isLoaded("polytone");

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (CHAIN_LIGHTS) ChainHangingLights.tick();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        if (CHAIN_LIGHTS) ChainHangingLights.clear();
    }
}
