package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.cirrus.CirrusLeaks;
import com.evandev.spicedcider.compat.exposure.ExposureLeaks;
import com.evandev.spicedcider.compat.modernfix.ModernFixLeaks;
import com.evandev.spicedcider.compat.nomansland.NoMansLandClientLeaks;
import com.evandev.spicedcider.compat.nomansland.NoMansLandLeaks;
import com.evandev.spicedcider.compat.voicechat.VoiceChatLeaks;
import com.evandev.spicedcider.util.ClientCacheRegistry;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ServerLeakEvents {

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ModList mods = ModList.get();
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (mods.isLoaded("nomansland")) NoMansLandLeaks.release(level);
            if (mods.isLoaded("modernfix")) ModernFixLeaks.release(level);
        }
        if (mods.isLoaded("exposure")) ExposureLeaks.release();
        if (mods.isLoaded("voicechat")) VoiceChatLeaks.release();
    }

    public static void onClientLoggedOut() {
        ModList mods = ModList.get();
        if (mods.isLoaded("cirrus")) CirrusLeaks.releaseClient();
        if (mods.isLoaded("nomansland")) NoMansLandClientLeaks.releaseClient();
        ClientCacheRegistry.clearAll();
    }
}
