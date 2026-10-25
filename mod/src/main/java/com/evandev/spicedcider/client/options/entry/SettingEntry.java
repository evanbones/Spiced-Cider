package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.SnapSink;
import com.evandev.spicedcider.client.options.setting.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Locale;

public class SettingEntry extends Entry {

    public static final int CONTROL_WIDTH = 120;

    private final Setting setting;
    private final ResetButton resetButton;
    private AbstractWidget widget;

    public SettingEntry(Setting setting) {
        this.setting = setting;
        this.widget = setting.createWidget(CONTROL_WIDTH);
        this.resetButton = new ResetButton(button -> {
            setting.reset();
            widget = setting.createWidget(CONTROL_WIDTH);
        });
    }

    public Setting setting() {
        return setting;
    }

    @Override
    public int height() {
        return 24;
    }

    private boolean showReset() {
        return setting.canReset() && !setting.isDefault() && setting.isActive();
    }

    private boolean labelHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y + 2 && mouseY < y + height() - 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int controlX = x + width - CONTROL_WIDTH;
        boolean active = setting.isActive();
        Component name = setting.name();
        int color = !active ? TEXT_DIM : labelHovered(mouseX, mouseY) ? TEXT_HOVER : TEXT;
        graphics.drawString(font, name, x, y + 8, color);

        boolean reset = showReset();
        int lineEnd = controlX - 6 - (reset ? 22 : 0);
        int lineStart = x + font.width(name) + 6;
        if (lineEnd > lineStart) {
            graphics.fill(lineStart, y + 12, lineEnd, y + 13, 0x5F7F7F7F);
        }

        if (reset) {
            resetButton.setPosition(controlX - 22, y + 2);
            resetButton.render(graphics, mouseX, mouseY, partialTick);
        }

        widget.setPosition(controlX, y + 2);
        widget.active = active;
        setting.beforeRender(widget);
        widget.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (showReset() && resetButton.isMouseOver(mouseX, mouseY)) {
            return resetButton.mouseClicked(mouseX, mouseY, button);
        }
        if (widget.isMouseOver(mouseX, mouseY)) {
            if (widget.mouseClicked(mouseX, mouseY, button)) return true;
            if (button == 1 && widget instanceof net.minecraft.client.gui.components.CycleButton<?> cycle && widget.active) {
                widget.playDownSound(minecraft.getSoundManager());
                cycle.mouseScrolled(mouseX, mouseY, 0, 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean handled = widget.mouseReleased(mouseX, mouseY, button);
        setting.afterRelease();
        return handled;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return widget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public List<FormattedCharSequence> tooltip(int mouseX, int mouseY) {
        if (!labelHovered(mouseX, mouseY) || (showReset() && resetButton.isMouseOver(mouseX, mouseY))) return List.of();
        return setting.tooltip();
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        snapTo(sink, widget);
        if (showReset()) snapTo(sink, resetButton);
    }

    @Override
    public boolean matches(String query) {
        return setting.name().getString().toLowerCase(Locale.ROOT).contains(query);
    }
}
