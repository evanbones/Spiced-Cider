package com.evandev.spicedcider.compat.quark.client;

import com.evandev.spicedcider.SpicedCider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class HotbarChangerLiftEvents {
    private static final boolean QUARK = ModList.get().isLoaded("quark");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLayerPre(RenderGuiLayerEvent.Pre event) {
        if (QUARK) HotbarChangerLift.pre(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLayerPreCancelled(RenderGuiLayerEvent.Pre event) {
        if (QUARK) HotbarChangerLift.cancelled(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLayerPost(RenderGuiLayerEvent.Post event) {
        if (QUARK) HotbarChangerLift.post(event);
    }
}
