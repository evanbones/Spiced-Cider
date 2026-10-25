package com.evandev.spicedcider.client.options.setting;

import com.evandev.spicedcider.client.options.PackDefaults;
import com.evandev.spicedcider.mixin.minecraft.client.OptionInstanceAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Objects;

public final class VanillaSetting<T> implements Setting {

    private final OptionInstance<T> option;
    private final OptionInstanceAccessor<T> accessor;

    @SuppressWarnings("unchecked")
    public VanillaSetting(OptionInstance<T> option) {
        this.option = option;
        this.accessor = (OptionInstanceAccessor<T>) (Object) option;
    }

    public OptionInstance<T> option() {
        return option;
    }

    @Override
    public Component name() {
        return accessor.spicedcider$caption();
    }

    @Override
    public List<FormattedCharSequence> tooltip() {
        Tooltip tooltip = accessor.spicedcider$tooltip().apply(option.get());
        return tooltip == null ? List.of() : tooltip.toCharSequence(Minecraft.getInstance());
    }

    private T defaultValue() {
        return PackDefaults.value(option).orElseGet(accessor::spicedcider$initialValue);
    }

    @Override
    public boolean isDefault() {
        return Objects.equals(option.get(), defaultValue());
    }

    @Override
    public boolean canReset() {
        return true;
    }

    @Override
    public void reset() {
        option.set(defaultValue());
    }

    @Override
    public AbstractWidget createWidget(int width) {
        return option.createButton(Minecraft.getInstance().options, 0, 0, width);
    }

    @Override
    public void beforeRender(AbstractWidget widget) {
        widget.setTooltip(null);
        widget.setMessage(ValueText.strip(widget.getMessage(), name()));
    }
}
