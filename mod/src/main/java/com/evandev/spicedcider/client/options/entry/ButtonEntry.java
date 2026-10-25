package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.SnapSink;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

public class ButtonEntry extends Entry {

    private final Button button;
    private final BooleanSupplier active;

    public ButtonEntry(Component label, Runnable action) {
        this(label, action, () -> true);
    }

    public ButtonEntry(Component label, Runnable action, BooleanSupplier active) {
        this.button = Button.builder(label, b -> action.run()).size(200, 20).build();
        this.active = active;
    }

    @Override
    public int height() {
        return 24;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        button.active = active.getAsBoolean();
        button.setWidth(Math.min(200, width));
        button.setPosition(x + (width - button.getWidth()) / 2, y + 2);
        button.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        snapTo(sink, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.button.isMouseOver(mouseX, mouseY) && this.button.mouseClicked(mouseX, mouseY, button);
    }
}
