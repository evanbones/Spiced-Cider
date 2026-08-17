package com.evandev.spicedcider.client.progress;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class ProgressRenderer {

    public static void drawHeaderText(GuiGraphics graphics, Component header, int width) {
        Font font = Minecraft.getInstance().font;
        int guiHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        graphics.drawCenteredString(font, header, width / 2, guiHeight / 2 - 4 - 16, 0xFFFFFF);
    }

    public static void drawStageText(GuiGraphics graphics, Component stage, int width) {
        Font font = Minecraft.getInstance().font;
        int guiHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        graphics.drawCenteredString(font, stage, width / 2, guiHeight / 2 - 4 + 8, 0xFFFFFF);
    }

    public static void renderProgressWithInt(int progress) {
        render(progress);
    }

    private static void render(int progress) {
        Window window = Minecraft.getInstance().getWindow();

        int xOffset = 100;
        int yOffset = 2;
        int xStart = window.getGuiScaledWidth() / 2 - xOffset / 2;
        int yStart = window.getGuiScaledHeight() / 2 + 16;

        if (progress >= xOffset) {
            progress = xOffset;
        }

        if (progress < 0) {
            progress = 0;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        fillQuad(builder, xStart, yStart, xStart + xOffset, yStart + yOffset, 128, 128, 128, 255);
        fillQuad(builder, xStart, yStart, xStart + progress, yStart + yOffset, 128, 255, 128, 255);

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableBlend();
    }

    private static void fillQuad(BufferBuilder builder, int minX, int minY, int maxX, int maxY, int r, int g, int b, int a) {
        builder.addVertex(minX, maxY, 0.0F).setColor(r, g, b, a);
        builder.addVertex(maxX, maxY, 0.0F).setColor(r, g, b, a);
        builder.addVertex(maxX, minY, 0.0F).setColor(r, g, b, a);
        builder.addVertex(minX, minY, 0.0F).setColor(r, g, b, a);
    }
}
