package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.voxyworldgen.VoxyWorldGenEdge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class GenerationEdgeEvents {
    private static final boolean VOXY_WORLDGEN = ModList.get().isLoaded("voxyworldgenv2");
    private static final int INTERVAL = 10;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!VOXY_WORLDGEN || event.getServer().getTickCount() % INTERVAL != 0) return;
        VoxyWorldGenEdge.tick(event.getServer());
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (VOXY_WORLDGEN) VoxyWorldGenEdge.forget(event.getEntity().getUUID());
    }
}
