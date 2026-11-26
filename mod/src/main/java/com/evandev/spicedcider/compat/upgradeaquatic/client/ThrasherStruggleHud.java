package com.evandev.spicedcider.compat.upgradeaquatic.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.networking.ThrasherStrugglePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ThrasherStruggleHud {
    private static final ResourceLocation BACKGROUND = ResourceLocation.withDefaultNamespace("hud/jump_bar_background");
    private static final ResourceLocation PROGRESS = ResourceLocation.withDefaultNamespace("hud/jump_bar_progress");
    private static float target = -1.0F;
    private static float shown;
    private static float shownO;

    public static void receive(ThrasherStrugglePayload payload) {
        if (target < 0.0F && payload.progress() >= 0.0F) {
            shown = shownO = payload.progress();
        }
        target = payload.progress();
    }

    private static boolean active() {
        Minecraft mc = Minecraft.getInstance();
        return target >= 0.0F && mc.player != null && mc.player.getVehicle() != null;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        shownO = shown;
        if (target >= 0.0F) shown += (target - shown) * 0.5F;
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        target = -1.0F;
    }

    @SubscribeEvent
    public static void onRegisterLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "thrasher_struggle"), ThrasherStruggleHud::renderLayer);
    }

    @SubscribeEvent
    public static void onRenderLayer(RenderGuiLayerEvent.Pre event) {
        if (active() && (event.getName().equals(VanillaGuiLayers.EXPERIENCE_LEVEL) || event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR))) {
            event.setCanceled(true);
        }
    }

    private static void renderLayer(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (!active() || mc.options.hideGui || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) return;
        render(graphics, deltaTracker.getGameTimeDeltaPartialTick(false));
    }

    private static void render(GuiGraphics graphics, float partialTick) {
        float progress = Mth.clamp(Mth.lerp(partialTick, shownO, shown), 0.0F, 1.0F);
        int x = graphics.guiWidth() / 2 - 91;
        int y = graphics.guiHeight() / 2 + 24;
        graphics.blitSprite(BACKGROUND, x, y, 182, 5);
        int width = (int) (progress * 183.0F);
        if (width > 0) {
            graphics.blitSprite(PROGRESS, 182, 5, 0, 0, x, y, width, 5);
        }
    }
}
