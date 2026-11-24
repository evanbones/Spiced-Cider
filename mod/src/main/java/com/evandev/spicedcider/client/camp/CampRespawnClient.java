package com.evandev.spicedcider.client.camp;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.networking.CampSpawnPayload;
import com.evandev.spicedcider.networking.RespawnChoicePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.WeakHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class CampRespawnClient {
    private static final int ROW = 24;
    private static boolean enabled;
    private static boolean hasBed;
    private static boolean hasCamp;
    private static final WeakHashMap<DeathScreen, Button> TENT_BUTTONS = new WeakHashMap<>();

    public static void receive(CampSpawnPayload payload) {
        enabled = payload.enabled();
        hasBed = payload.hasBed();
        hasCamp = payload.hasCamp();
    }

    @SubscribeEvent
    public static void onInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof DeathScreen screen) || !enabled) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.level.getLevelData().isHardcore()) return;
        Button respawn = find(screen, "deathScreen.respawn", "gui.spicedcider.respawn_bed");
        if (respawn == null) return;
        if (hasBed) respawn.setMessage(Component.translatable("gui.spicedcider.respawn_bed"));
        Button tent = Button.builder(Component.translatable("gui.spicedcider.respawn_tent"), button -> {
            if (minecraft.player == null) return;
            PacketDistributor.sendToServer(new RespawnChoicePayload(true));
            minecraft.player.respawn();
            button.active = false;
            respawn.active = false;
        }).bounds(respawn.getX(), respawn.getY() + ROW, respawn.getWidth(), respawn.getHeight()).build();
        tent.active = false;
        event.addListener(tent);
        Button title = find(screen, "deathScreen.titleScreen");
        if (title != null) title.setTabOrderGroup(1);
        TENT_BUTTONS.put(screen, tent);
    }

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Pre event) {
        if (!(event.getScreen() instanceof DeathScreen screen)) return;
        Button tent = TENT_BUTTONS.get(screen);
        if (tent == null) return;
        Button respawn = find(screen, "deathScreen.respawn", "gui.spicedcider.respawn_bed");
        if (respawn == null) return;
        tent.setX(respawn.getX());
        tent.setY(respawn.getY() + ROW);
        tent.setWidth(respawn.getWidth());
        tent.active = hasCamp && respawn.active;
        Button title = find(screen, "deathScreen.titleScreen");
        if (title != null && title.getY() < tent.getY() + ROW && title.getY() + title.getHeight() > tent.getY()) {
            title.setY(tent.getY() + ROW);
        }
    }

    private static Button find(DeathScreen screen, String... keys) {
        for (GuiEventListener listener : screen.children()) {
            if (!(listener instanceof Button button) || TENT_BUTTONS.containsValue(button)) continue;
            if (button.getMessage().getContents() instanceof TranslatableContents contents) {
                for (String key : keys) {
                    if (contents.getKey().equals(key)) return button;
                }
            }
        }
        return null;
    }
}
