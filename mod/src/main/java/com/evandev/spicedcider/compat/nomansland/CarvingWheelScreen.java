package com.evandev.spicedcider.compat.nomansland;

import com.evandev.spicedcider.client.gui.WheelButtons;
import com.evandev.spicedcider.client.gui.WheelLabel;
import com.farcr.nomansland.client.handler.CarvingClientHandler;
import com.farcr.nomansland.common.carving.CarvingType;
import com.farcr.nomansland.common.registry.NMLRegistries;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class CarvingWheelScreen extends Screen {
    private static final float DEADZONE = 12.0F;
    private static final float LABEL_MARGIN = 3.0F;
    private static final int LABEL_WRAP = 96;
    private static final float OPEN_MILLIS = 250.0F;

    @Nullable
    private static CarvingType lastSelected;

    private final BlockPos startPos;
    private final Direction face;
    private final InteractionHand hand;
    private final List<CarvingType> types;
    private final long openedAt = Util.getMillis();
    private int selected;
    private float radius = -1.0F;
    private float labelScale;

    public CarvingWheelScreen(BlockPos startPos, Direction face, InteractionHand hand, List<CarvingType> types) {
        super(Component.translatable("screen.nomansland.carving"));
        this.startPos = startPos;
        this.face = face;
        this.hand = hand;
        this.types = types;
        this.selected = Math.max(0, types.indexOf(lastSelected));
    }

    public void point(float x, float y) {
        if (x * x + y * y < DEADZONE * DEADZONE) return;
        select(WheelButtons.sector(x, y, types.size()));
    }

    public void select(int index) {
        if (index == selected || index < 0 || index >= types.size()) return;
        selected = index;
        lastSelected = types.get(index);
        if (minecraft != null && minecraft.player != null) minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.1F);
    }

    public void confirm() {
        CarvingType type = types.get(selected);
        lastSelected = type;
        CarvingClientHandler.instance.startChisel(startPos, face, hand, NMLRegistries.CARVING_TYPE.getKey(type));
        onClose();
    }

    private List<FormattedCharSequence> label(int index) {
        return WheelLabel.split(font, types.get(index).getIcon().getHoverName(), LABEL_WRAP);
    }

    private void layout() {
        labelScale = WheelLabel.snap(WheelLabel.SCALE);
        float needed = 0.0F;
        for (int index = 0; index < types.size(); index++) needed = Math.max(needed, WheelLabel.reach(font, label(index), labelScale));
        radius = Math.max(WheelButtons.ringRadius(types.size()), needed + WheelButtons.SIZE / 2.0F + LABEL_MARGIN);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (radius < 0.0F) layout();
        float progress = Mth.clamp((Util.getMillis() - openedAt) / OPEN_MILLIS, 0.0F, 1.0F);
        float ease = 1.0F - (1.0F - progress) * (1.0F - progress);
        float centreX = width / 2.0F;
        float centreY = height / 2.0F;
        RenderSystem.enableBlend();
        for (int index = 0; index < types.size(); index++) {
            WheelButtons.render(graphics, types.get(index).getIcon(), index, types.size(), radius * ease, centreX, centreY, index == selected);
        }
        WheelLabel.render(graphics, font, label(selected), centreX, centreY, progress < 1.0F ? labelScale * ease : labelScale);
        RenderSystem.disableBlend();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        point((float) (mouseX - width / 2.0), (float) (mouseY - height / 2.0));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT && button != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return super.mouseClicked(mouseX, mouseY, button);
        point((float) (mouseX - width / 2.0), (float) (mouseY - height / 2.0));
        confirm();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0.0) select(Math.floorMod(selected - (int) Math.signum(scrollY), types.size()));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            select(keyCode - GLFW.GLFW_KEY_1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
            confirm();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
