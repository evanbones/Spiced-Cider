package com.evandev.spicedcider.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

public final class WheelLabel {
    public static final float SCALE = 0.75F;
    public static final int MAX_LINES = 2;

    public static float snap(float scale) {
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        return (float) (Math.max(1L, Math.round(scale * guiScale)) / guiScale);
    }

    public static List<FormattedCharSequence> split(Font font, Component text, int wrap) {
        List<FormattedCharSequence> lines = font.split(text, Math.max(1, wrap));
        return lines.size() > MAX_LINES ? lines.subList(0, MAX_LINES) : lines;
    }

    public static float reach(Font font, List<FormattedCharSequence> lines, float scale) {
        int width = 0;
        for (FormattedCharSequence line : lines) width = Math.max(width, font.width(line));
        float halfWidth = (width / 2.0F + 4.0F) * scale;
        float halfHeight = (lines.size() * font.lineHeight / 2.0F + 4.0F) * scale;
        return Mth.sqrt(halfWidth * halfWidth + halfHeight * halfHeight);
    }

    public static void render(GuiGraphics graphics, Font font, List<FormattedCharSequence> lines, float centreX, float centreY, float scale) {
        if (lines.isEmpty()) return;
        int width = 0;
        for (FormattedCharSequence line : lines) width = Math.max(width, font.width(line));
        int height = lines.size() * font.lineHeight - 1;
        graphics.pose().pushPose();
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        graphics.pose().translate((float) (Math.round(centreX * guiScale) / guiScale), (float) (Math.round(centreY * guiScale) / guiScale), 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        TooltipRenderUtil.renderTooltipBackground(graphics, -width / 2, -height / 2, width, height, 400);
        graphics.pose().translate(0.0F, 0.0F, 400.0F);
        for (int line = 0; line < lines.size(); line++) {
            FormattedCharSequence text = lines.get(line);
            graphics.drawString(font, text, -font.width(text) / 2, -height / 2 + line * font.lineHeight, 0xFFFFFF);
        }
        graphics.pose().popPose();
    }
}
