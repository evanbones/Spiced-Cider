package com.evandev.spicedcider.client.tips;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class BedChatOffset {
    private static final int GAP = 4;

    @SubscribeEvent
    public static void onChat(CustomizeGuiOverlayEvent.Chat event) {
        if (!(Minecraft.getInstance().screen instanceof InBedChatScreen)) return;
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.bedScreenTips, false)) return;
        int top = Tips.renderedTop();
        if (top >= 0 && event.getPosY() > top - GAP) {
            event.setPosY(top - GAP);
        }
    }
}
