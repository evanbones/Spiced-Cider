package com.evandev.spicedcider.client.options.entry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextEntry extends Entry {

    private final Component text;

    public TextEntry(Component text) {
        this.text = text;
    }

    @Override
    public int height() {
        return 20;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawCenteredString(font, text, x + width / 2, y + 6, TEXT_DIM);
    }
}
