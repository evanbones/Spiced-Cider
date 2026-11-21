package com.evandev.spicedcider.compat.personality;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.blaze3d.platform.InputConstants;
import com.teamabnormals.personality.core.other.PersonalityKeyBindings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class StanceCycle {
    public static final KeyMapping KEY = new KeyMapping("key.spicedcider.cycle_stance", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "key.categories.movement");
    private static final int CRAWL_DELAY_TICKS = 2;

    private static int crawlDelay = -1;

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        if (ModList.get().isLoaded("personality")) event.register(KEY);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (!ModList.get().isLoaded("personality")) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            crawlDelay = -1;
            return;
        }
        if (crawlDelay >= 0 && crawlDelay-- == 0) toggleCrawl();
        while (KEY.consumeClick()) cycle(player);
    }

    private static void cycle(LocalPlayer player) {
        if (crawlDelay >= 0) return;
        if (isCrawling(player)) {
            toggleCrawl();
        } else if (PersonalityKeyBindings.SIT.isDown()) {
            setSitting(false);
            crawlDelay = CRAWL_DELAY_TICKS;
        } else {
            setSitting(true);
        }
    }

    private static boolean isCrawling(LocalPlayer player) {
        return player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isFallFlying();
    }

    private static void setSitting(boolean sitting) {
        KeyMapping sit = PersonalityKeyBindings.SIT;
        if (sit.isDown() == sitting) return;
        sit.setDown(PersonalityKeyBindings.TOGGLE_SIT.get() || sitting);
    }

    private static void toggleCrawl() {
        KeyMapping crawl = PersonalityKeyBindings.CRAWL;
        boolean on = !crawl.isDown();
        crawl.setDown(PersonalityKeyBindings.TOGGLE_CRAWL.get() || on);
    }
}
