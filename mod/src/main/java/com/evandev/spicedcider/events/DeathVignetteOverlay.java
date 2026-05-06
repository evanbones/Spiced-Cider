package com.evandev.spicedcider.events;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SpicedCider.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class DeathVignetteOverlay {

    private static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");
    private static final float MAX_ANIMATION_TICKS = 60.0f;
    private static int deathTicks = 0;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.isDeadOrDying()) {
            deathTicks++;
        } else {
            deathTicks = 0;
            return;
        }

        float progress = Mth.clamp(deathTicks / MAX_ANIMATION_TICKS, 0.0f, 1.0f);
        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        if (progress >= 1.0f) {
            graphics.fill(0, 0, screenWidth, screenHeight, 0xFF000000);
        } else {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, progress);
            graphics.blit(VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            // TODO: Render a growing solid black border to simulate it closing in?
        }
    }
}