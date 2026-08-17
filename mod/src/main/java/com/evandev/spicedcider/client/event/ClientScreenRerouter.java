package com.evandev.spicedcider.client.event;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.progress.OldLoadingScreen;
import com.evandev.spicedcider.client.progress.OldProgressScreen;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.minecraft.client.LevelLoadingScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ClientScreenRerouter {

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!SpicedCiderConfig.CLIENT.oldProgressScreen.get()) return;

        Screen screen = event.getScreen();
        if (screen == null) return;

        if (screen instanceof ProgressScreen progressScreen && !(screen instanceof OldProgressScreen)) {
            event.setNewScreen(new OldProgressScreen(progressScreen));
            return;
        }

        if (screen instanceof LevelLoadingScreen levelLoadingScreen) {
            StoringChunkProgressListener progressListener = ((LevelLoadingScreenAccessor) levelLoadingScreen).spicedcider$getProgressListener();
            if (progressListener == null) {
                progressListener = Minecraft.getInstance().getProgressListener();
            }

            Component header = Component.translatable("gui.spicedcider.screen.level.loading");
            Component stage = Component.translatable("gui.spicedcider.screen.level.building");

            if (progressListener != null) {
                event.setNewScreen(new OldLoadingScreen(progressListener, header, stage));
            }
            return;
        }

        if (screen instanceof GenericMessageScreen) {
            String title = screen.getTitle().getString();
            OldProgressScreen oldProgressScreen = getProgressScreenForTitle(title);
            if (oldProgressScreen.hasStage()) {
                event.setNewScreen(oldProgressScreen);
            }
        }
    }

    private static OldProgressScreen getProgressScreenForTitle(String title) {
        OldProgressScreen progressScreen = new OldProgressScreen(new ProgressScreen(false));

        String savingLevelStr = Component.translatable("menu.savingLevel").getString();
        String resourceLoadStr = Component.translatable("selectWorld.resource_load").getString();
        String dataReadStr = Component.translatable("selectWorld.data_read").getString();

        if (title.equals("Saving level") || title.equals(savingLevelStr)) {
            progressScreen.setStage(Component.translatable("gui.spicedcider.screen.level.saving"));
        } else if (title.equals(resourceLoadStr)) {
            progressScreen.setHeader(Component.translatable("gui.spicedcider.screen.level.loading"));
            progressScreen.setStage(Component.translatable("selectWorld.resource_load"));
        } else if (title.equals(dataReadStr)) {
            progressScreen.setHeader(Component.translatable("gui.spicedcider.screen.level.loading"));
            progressScreen.setStage(Component.translatable("selectWorld.data_read"));
        }

        return progressScreen;
    }
}
