package com.evandev.spicedcider.client.gui;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public final class WheelButtons {
    public static final int SIZE = 32;
    private static final ResourceLocation BUTTONS = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/gui/shape_wheel/buttons.png");
    private static final float PITCH = Mth.sqrt(2048.0F) + 8.0F;
    private static final float MIN_RADIUS = 43.0F;

    public static float ringRadius(int count) {
        return Math.max(MIN_RADIUS, count * PITCH / (Mth.PI * 2.0F));
    }

    public static int sector(float x, float y, int count) {
        double turn = Math.atan2(x, -y) / (Math.PI * 2.0);
        return Math.floorMod(Math.round((float) (turn * count)), count);
    }

    public static void render(GuiGraphics graphics, ItemStack stack, int index, int count, float radius, float centreX, float centreY, boolean selected) {
        float angle = Mth.PI * 2.0F * index / count;
        graphics.pose().pushPose();
        graphics.pose().translate(centreX + Mth.sin(angle) * radius - SIZE / 2.0F, centreY - Mth.cos(angle) * radius - SIZE / 2.0F, 0.0F);
        graphics.blit(BUTTONS, 0, 0, selected ? SIZE : 0.0F, 0.0F, SIZE, SIZE, SIZE * 2, SIZE);
        graphics.pose().translate(4.0F, 4.0F, 0.0F);
        graphics.pose().scale(1.5F, 1.5F, 1.0F);
        graphics.renderFakeItem(stack, 0, 0);
        graphics.pose().popPose();
    }
}
