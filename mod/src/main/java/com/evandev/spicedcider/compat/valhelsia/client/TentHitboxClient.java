package com.evandev.spicedcider.compat.valhelsia.client;

import com.evandev.spicedcider.SpicedCider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class TentHitboxClient {
    private static final boolean VALHELSIA = ModList.get().isLoaded("valhelsia_structures");

    @SubscribeEvent
    public static void onRenderHighlight(RenderHighlightEvent.Block event) {
        if (VALHELSIA && TentHitbox.renderOutline(event)) event.setCanceled(true);
    }
}
