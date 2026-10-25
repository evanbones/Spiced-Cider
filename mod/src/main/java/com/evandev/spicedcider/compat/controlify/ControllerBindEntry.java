package com.evandev.spicedcider.compat.controlify;

import com.evandev.spicedcider.client.options.SettingsScreen;
import com.evandev.spicedcider.client.options.SnapSink;
import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.ResetButton;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.bindings.input.EmptyInput;
import dev.isxander.controlify.controller.ControllerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ControllerBindEntry extends Entry {

    private final SettingsScreen screen;
    private final ControllerEntity controller;
    private final InputBinding binding;
    private final Collection<InputBinding> all;
    private final Button bindButton;
    private final ResetButton resetButton;
    private final List<Component> conflicts = new ArrayList<>();

    public ControllerBindEntry(SettingsScreen screen, ControllerEntity controller, InputBinding binding, Collection<InputBinding> all) {
        this.screen = screen;
        this.controller = controller;
        this.binding = binding;
        this.all = all;
        this.bindButton = Button.builder(Component.empty(), button -> screen.beginCapture(binding.name(), binding))
                .size(SettingEntry.CONTROL_WIDTH, 20)
                .build();
        this.resetButton = new ResetButton(button -> {
            binding.setBoundInput(binding.defaultInput());
            ControlifySettings.save();
        });
    }

    @Override
    public int height() {
        return 24;
    }

    private boolean isDefault() {
        return binding.boundInput().equals(binding.defaultInput());
    }

    private boolean labelHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y + 2 && mouseY < y + height() - 2;
    }

    private void updateConflicts() {
        conflicts.clear();
        if (binding.boundInput() instanceof EmptyInput) return;
        for (InputBinding other : all) {
            if (other == binding || !other.boundInput().equals(binding.boundInput())) continue;
            if (other.contexts().stream().anyMatch(binding.contexts()::contains)) conflicts.add(other.name());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateConflicts();
        int controlX = x + width - SettingEntry.CONTROL_WIDTH;
        Component name = binding.name();
        graphics.drawString(font, name, x, y + 8, labelHovered(mouseX, mouseY) ? TEXT_HOVER : TEXT);

        boolean reset = !isDefault();
        int lineEnd = controlX - 6 - (reset ? 22 : 0);
        int lineStart = x + font.width(name) + 6;
        if (lineEnd > lineStart) {
            graphics.fill(lineStart, y + 12, lineEnd, y + 13, 0x5F7F7F7F);
        }
        if (reset) {
            resetButton.setPosition(controlX - 22, y + 2);
            resetButton.render(graphics, mouseX, mouseY, partialTick);
        }

        MutableComponent label;
        if (screen.captureToken() == binding) {
            label = Component.translatable("controlify.gui.bind_input_awaiting").withStyle(ChatFormatting.YELLOW);
        } else if (binding.boundInput() instanceof EmptyInput) {
            label = Component.translatable("key.keyboard.unknown").withStyle(ChatFormatting.GRAY);
        } else {
            label = Controlify.instance().inputFontMapper().getComponentFromBind(controller.info().type().namespace(), binding.boundInput()).copy();
            if (!conflicts.isEmpty()) label = Component.literal("[ ").append(label).append(" ]").withStyle(ChatFormatting.RED);
        }
        bindButton.setMessage(label);
        bindButton.setPosition(controlX, y + 2);
        bindButton.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isDefault() && resetButton.isMouseOver(mouseX, mouseY)) return resetButton.mouseClicked(mouseX, mouseY, button);
        if (bindButton.isMouseOver(mouseX, mouseY)) {
            if (button == 1) {
                binding.setBoundInput(EmptyInput.INSTANCE);
                ControlifySettings.save();
                return true;
            }
            return bindButton.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public List<FormattedCharSequence> tooltip(int mouseX, int mouseY) {
        if (!labelHovered(mouseX, mouseY) || (!isDefault() && resetButton.isMouseOver(mouseX, mouseY))) return List.of();
        boolean generated = binding.description().getContents() instanceof TranslatableContents contents
                && contents.getKey().equals("controlify.custom_binding.vanilla_description");
        if (generated && conflicts.isEmpty()) return List.of();
        MutableComponent text = generated ? Component.empty() : binding.description().copy();
        if (!conflicts.isEmpty()) {
            if (!generated) text.append("\n");
            text.append(Component.translatable("spicedcider.options.key.conflict").withStyle(ChatFormatting.RED));
            for (int i = 0; i < conflicts.size(); i++) {
                text.append(i == 0 ? " " : ", ").append(conflicts.get(i).copy().withStyle(ChatFormatting.GRAY));
            }
        }
        return font.split(text, 200);
    }

    @Override
    public void collectSnapPoints(SnapSink sink) {
        snapTo(sink, bindButton);
        if (!isDefault()) snapTo(sink, resetButton);
    }

    @Override
    public boolean matches(String query) {
        return binding.name().getString().toLowerCase(Locale.ROOT).contains(query);
    }
}
