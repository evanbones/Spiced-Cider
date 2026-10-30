package com.evandev.spicedcider.compat.clutternomore;

import com.evandev.spicedcider.client.gui.WheelButtons;
import com.evandev.spicedcider.client.gui.WheelLabel;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.shape.ShapeMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ShapeWheel {
    private static final float MAX_RADIUS = 72.0F;
    private static final float LABEL_MARGIN = 3.0F;
    private static final float[] LABEL_SCALES = {WheelLabel.SCALE, 0.625F, 0.5F};
    private static final int[] LABEL_WRAPS = {Integer.MAX_VALUE, 96, 72, 56};
    private static final float DEADZONE = 8.0F;
    private static final float MOUSE_SCALE = 0.2F;
    private static final float OPEN_MILLIS = 250.0F;

    @Nullable
    private static ShapeSwitcherOverlay owner;
    private static float cursorX;
    private static float cursorY;
    private static int pointed = -1;
    private static long openedAt;
    private static float layoutRadius = -1.0F;
    private static float labelScale;
    private static int labelWrap;

    @Nullable
    private static ShapeSwitcherOverlay open() {
        ShapeSwitcherOverlay overlay = ClutterNoMoreClient.getOverlay();
        if (overlay == null || !overlay.isVisible() || overlay.getShapeCount() < 2) return null;
        if (overlay != owner) {
            owner = overlay;
            cursorX = 0.0F;
            cursorY = 0.0F;
            pointed = -1;
            openedAt = Util.getMillis();
            layoutRadius = -1.0F;
        }
        return overlay;
    }

    public static boolean isShowing() {
        return open() != null;
    }

    public static boolean canChangeShape() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.screen == null && !isShowing() && ClutterNoMoreClient.shapeHand(minecraft) != null;
    }

    public static boolean onMouseTurn(double dx, double dy) {
        ShapeSwitcherOverlay overlay = open();
        if (overlay == null) return false;
        moveCursor(overlay, cursorX + (float) dx * MOUSE_SCALE, cursorY + (float) dy * MOUSE_SCALE);
        return true;
    }

    public static boolean onStick(double x, double y) {
        ShapeSwitcherOverlay overlay = open();
        if (overlay == null) return false;
        double length = Math.sqrt(x * x + y * y);
        if (length > 1.0E-3) {
            float radius = radius(overlay);
            moveCursor(overlay, (float) (x / length) * radius, (float) (y / length) * radius);
        }
        return true;
    }

    private static void moveCursor(ShapeSwitcherOverlay overlay, float x, float y) {
        float radius = radius(overlay);
        float length = Mth.sqrt(x * x + y * y);
        if (length > radius) {
            x *= radius / length;
            y *= radius / length;
            length = radius;
        }
        cursorX = x;
        cursorY = y;
        if (length < DEADZONE) return;

        int index = WheelButtons.sector(x, y, overlay.getShapeCount());
        if (index == pointed) return;
        pointed = index;
        if (index != overlay.getSelectedIndex()) overlay.changeSlot(index);
    }

    private static float radius(ShapeSwitcherOverlay overlay) {
        return layoutRadius > 0.0F ? layoutRadius : WheelButtons.ringRadius(overlay.getShapeCount());
    }

    private static void layout(ShapeSwitcherOverlay overlay, ItemStack held, Font font) {
        float ring = WheelButtons.ringRadius(overlay.getShapeCount());
        float buttonReach = WheelButtons.SIZE / 2.0F + LABEL_MARGIN;
        float best = Float.MAX_VALUE;
        for (float target : LABEL_SCALES) {
            float scale = WheelLabel.snap(target);
            for (int wrap : LABEL_WRAPS) {
                float needed = 0.0F;
                for (int index = 0; index < overlay.getShapeCount(); index++) {
                    List<FormattedCharSequence> lines = font.split(ShapeMap.transferStack(held, index).getHoverName(), wrap);
                    if (lines.size() > WheelLabel.MAX_LINES) {
                        needed = -1.0F;
                        break;
                    }
                    needed = Math.max(needed, WheelLabel.reach(font, lines, scale));
                }
                if (needed < 0.0F) continue;
                float radius = Math.max(ring, needed + buttonReach);
                if (radius <= MAX_RADIUS) {
                    layoutRadius = radius;
                    labelScale = scale;
                    labelWrap = wrap;
                    return;
                }
                if (radius < best) {
                    best = radius;
                    layoutRadius = Math.min(radius, MAX_RADIUS);
                    labelScale = scale;
                    labelWrap = wrap;
                }
            }
        }
    }

    public static boolean render(ShapeSwitcherOverlay overlay, GuiGraphics graphics, ItemStack held) {
        if (open() != overlay) return false;

        Font font = Minecraft.getInstance().font;
        if (layoutRadius < 0.0F) layout(overlay, held, font);

        int count = overlay.getShapeCount();
        int selected = overlay.getSelectedIndex();
        float progress = Mth.clamp((Util.getMillis() - openedAt) / OPEN_MILLIS, 0.0F, 1.0F);
        float ease = 1.0F - (1.0F - progress) * (1.0F - progress);
        float radius = layoutRadius * ease;
        float centreX = graphics.guiWidth() / 2.0F;
        float centreY = graphics.guiHeight() / 2.0F;

        RenderSystem.enableBlend();
        for (int index = 0; index < count; index++) {
            WheelButtons.render(graphics, ShapeMap.transferStack(held, index), index, count, radius, centreX, centreY, index == selected);
        }

        List<FormattedCharSequence> lines = font.split(ShapeMap.transferStack(held, selected).getHoverName(), labelWrap);
        WheelLabel.render(graphics, font, lines, centreX, centreY, progress < 1.0F ? labelScale * ease : labelScale);
        RenderSystem.disableBlend();
        return true;
    }
}
