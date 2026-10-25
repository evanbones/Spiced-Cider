package com.evandev.spicedcider.client.options.entry;

import com.evandev.spicedcider.client.options.KeyLayout;
import com.evandev.spicedcider.client.options.PackDefaults;
import com.evandev.spicedcider.client.options.SettingsScreen;
import com.evandev.spicedcider.client.options.SnapSink;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KeyEntry extends Entry {

    private final SettingsScreen screen;
    private final KeyMapping mapping;
    private final Button keyButton;
    private final ResetButton resetButton;
    private final List<Component> conflicts = new ArrayList<>();

    public KeyEntry(SettingsScreen screen, KeyMapping mapping) {
        this.screen = screen;
        this.mapping = mapping;
        this.keyButton = Button.builder(Component.empty(), button -> screen.startBinding(mapping))
                .size(SettingEntry.CONTROL_WIDTH, 20)
                .build();
        this.resetButton = new ResetButton(button -> {
            PackDefaults.resetKey(mapping);
            KeyMapping.resetMapping();
        });
    }

    public KeyMapping mapping() {
        return mapping;
    }

    @Override
    public int height() {
        return 24;
    }

    private boolean labelHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y + 2 && mouseY < y + height() - 2;
    }

    private void updateConflicts() {
        conflicts.clear();
        if (mapping.isUnbound()) return;
        for (KeyMapping other : minecraft.options.keyMappings) {
            if (other == mapping || KeyLayout.groupOf(other.getName()) == null || KeyLayout.sharesIntentionally(mapping.getName(), other.getName())) continue;
            boolean contextsOverlap = mapping.getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(mapping.getKeyConflictContext());
            if (contextsOverlap && (mapping.same(other) || other.hasKeyModifierConflict(mapping))) {
                conflicts.add(other.getDisplayName());
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateConflicts();
        int controlX = x + width - SettingEntry.CONTROL_WIDTH;
        Component name = mapping.getDisplayName();
        graphics.drawString(font, name, x, y + 8, labelHovered(mouseX, mouseY) ? TEXT_HOVER : TEXT);

        boolean reset = !PackDefaults.isKeyDefault(mapping);
        int lineEnd = controlX - 6 - (reset ? 22 : 0);
        int lineStart = x + font.width(name) + 6;
        if (lineEnd > lineStart) {
            graphics.fill(lineStart, y + 12, lineEnd, y + 13, 0x5F7F7F7F);
        }

        if (reset) {
            resetButton.setPosition(controlX - 22, y + 2);
            resetButton.render(graphics, mouseX, mouseY, partialTick);
        }

        MutableComponent label = mapping.getTranslatedKeyMessage().copy();
        if (screen.bindingKey() == mapping) {
            label = Component.literal("> ").append(label.withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE)).append(" <").withStyle(ChatFormatting.YELLOW);
        } else if (!conflicts.isEmpty()) {
            label = label.withStyle(ChatFormatting.RED);
        }
        keyButton.setMessage(label);
        keyButton.setPosition(controlX, y + 2);
        keyButton.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!PackDefaults.isKeyDefault(mapping) && resetButton.isMouseOver(mouseX, mouseY)) {
            return resetButton.mouseClicked(mouseX, mouseY, button);
        }
        return keyButton.isMouseOver(mouseX, mouseY) && keyButton.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public List<FormattedCharSequence> tooltip(int mouseX, int mouseY) {
        if (!labelHovered(mouseX, mouseY) || conflicts.isEmpty()) return List.of();
        if (!PackDefaults.isKeyDefault(mapping) && resetButton.isMouseOver(mouseX, mouseY)) return List.of();
        MutableComponent text = Component.translatable("spicedcider.options.key.conflict").withStyle(ChatFormatting.RED);
        for (int i = 0; i < conflicts.size(); i++) {
            text.append(i == 0 ? " " : ", ").append(conflicts.get(i).copy().withStyle(ChatFormatting.GRAY));
        }
        return font.split(text, 200);
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        snapTo(sink, keyButton);
        if (!PackDefaults.isKeyDefault(mapping)) snapTo(sink, resetButton);
    }

    @Override
    public boolean matches(String query) {
        return mapping.getDisplayName().getString().toLowerCase(Locale.ROOT).contains(query);
    }
}
