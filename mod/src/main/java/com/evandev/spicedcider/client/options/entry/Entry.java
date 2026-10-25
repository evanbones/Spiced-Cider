package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.SnapSink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public abstract class Entry {

    protected static final int TEXT = 0xFFFFFFFF;
    protected static final int TEXT_HOVER = 0xFFFFFFA0;
    protected static final int TEXT_DIM = 0xFF7F7F7F;

    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final Font font = minecraft.font;
    protected int x;
    protected int y;
    protected int width;

    public void setBounds(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public abstract int height();

    public boolean isVisibleBetween(int top, int bottom) {
        return y + height() >= top && y <= bottom;
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    public List<FormattedCharSequence> tooltip(int mouseX, int mouseY) {
        return List.of();
    }

    public boolean matches(String query) {
        return false;
    }

    public void tick() {
    }

    public void collectSnapPoints(SnapSink sink) {
    }

    protected static void snapTo(SnapSink sink, AbstractWidget widget) {
        sink.accept(widget.getX() + widget.getWidth() / 2, widget.getY() + widget.getHeight() / 2, Math.min(widget.getWidth(), widget.getHeight()) / 2);
    }
}
