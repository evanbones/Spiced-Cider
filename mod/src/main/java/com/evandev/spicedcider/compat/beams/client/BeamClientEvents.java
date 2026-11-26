package com.evandev.spicedcider.compat.beams.client;

import com.evandev.spicedcider.SpicedCider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import com.evandev.spicedcider.compat.beams.BeamRaytrace;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class BeamClientEvents {
    private static final boolean BEAMS = ModList.get().isLoaded("beams");

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!BEAMS) return;
        BeamPreviewRenderer.render(event);
        BeamCrackRenderer.render(event);
    }

    @SubscribeEvent
    public static void onRenderFramePre(RenderFrameEvent.Pre event) {
        if (BEAMS) BeamRaytrace.setClientPartialTick(event.getPartialTick().getGameTimeDeltaPartialTick(true));
    }

    @SubscribeEvent
    public static void onRenderFramePost(RenderFrameEvent.Post event) {
        if (BEAMS) BeamRaytrace.setClientPartialTick(1.0F);
    }
}
