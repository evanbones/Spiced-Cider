package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.SnapSink;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SearchEntry extends Entry {

    private final EditBox box;

    public SearchEntry(String value, Consumer<String> onChange) {
        this.box = new EditBox(font, 0, 0, 200, 20, Component.translatable("spicedcider.options.search"));
        this.box.setHint(Component.translatable("spicedcider.options.search.hint"));
        this.box.setMaxLength(64);
        this.box.setValue(value);
        this.box.setResponder(onChange);
        this.box.setFocused(true);
    }

    @Override
    public int height() {
        return 28;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        box.setWidth(width - 2);
        box.setPosition(x + 1, y + 4);
        box.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        snapTo(sink, box);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = box.mouseClicked(mouseX, mouseY, button);
        box.setFocused(true);
        return clicked;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return box.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return box.charTyped(codePoint, modifiers);
    }
}
