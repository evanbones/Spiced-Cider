package com.evandev.spicedcider.client.options.setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public final class GuiScaleSetting implements Setting {

    private final VanillaSetting<Integer> vanilla;
    private int pending;

    public GuiScaleSetting() {
        this.vanilla = new VanillaSetting<>(Minecraft.getInstance().options.guiScale());
    }

    @Override
    public Component name() {
        return vanilla.name();
    }

    @Override
    public List<FormattedCharSequence> tooltip() {
        return vanilla.tooltip();
    }

    @Override
    public boolean isDefault() {
        return vanilla.isDefault();
    }

    @Override
    public boolean canReset() {
        return true;
    }

    @Override
    public void reset() {
        vanilla.reset();
    }

    @Override
    public AbstractWidget createWidget(int width) {
        Minecraft minecraft = Minecraft.getInstance();
        int current = vanilla.option().get();
        int max = Math.max(current, minecraft.getWindow().calculateScale(0, minecraft.isEnforceUnicode()));
        pending = current;
        return new StepSlider(width, 0, max, 1, current,
                value -> value == 0 ? Component.translatable("options.guiScale.auto") : Component.translatable("spicedcider.options.value.scale", value),
                value -> pending = value,
                () -> vanilla.option().set(pending));
    }
}
