package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.SnapSink;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LanguageEntry extends Entry {

    private final String code;
    private final LanguageInfo info;
    private final Supplier<String> selected;
    private final Consumer<String> select;

    public LanguageEntry(String code, LanguageInfo info, Supplier<String> selected, Consumer<String> select) {
        this.code = code;
        this.info = info;
        this.selected = selected;
        this.select = select;
    }

    @Override
    public int height() {
        return 18;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean isSelected = code.equals(selected.get());
        boolean hovered = isMouseOver(mouseX, mouseY);
        if (isSelected) {
            graphics.fill(x, y, x + width, y + 16, 0xFF808080);
            graphics.fill(x + 1, y + 1, x + width - 1, y + 15, 0xFF000000);
        } else if (hovered) {
            graphics.fill(x, y, x + width, y + 16, 0x20FFFFFF);
        }
        graphics.drawCenteredString(font, info.toComponent(), x + width / 2, y + 4, hovered ? TEXT_HOVER : TEXT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        if (!code.equals(selected.get())) {
            select.accept(code);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        return true;
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        sink.accept(x + width / 2, y + 8, 8);
    }

    @Override
    public boolean matches(String query) {
        return info.toComponent().getString().toLowerCase(Locale.ROOT).contains(query);
    }
}
