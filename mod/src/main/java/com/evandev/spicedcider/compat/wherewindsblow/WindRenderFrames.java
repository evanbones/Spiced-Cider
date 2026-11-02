package com.evandev.spicedcider.compat.wherewindsblow;

import com.evandev.spicedcider.SpicedCider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class WindRenderFrames {
    private static long frame;
    private static long interactorFrame = -1L;

    @SubscribeEvent
    public static void onFrame(RenderFrameEvent.Pre event) {
        frame++;
    }

    public static boolean claimInteractorUpdate() {
        if (interactorFrame == frame) return false;
        interactorFrame = frame;
        return true;
    }
}
