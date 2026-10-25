package com.evandev.spicedcider.client.options.setting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CycleSetting<T> implements Setting {

    private final Component name;
    private final Supplier<List<T>> values;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Function<T, Component> display;
    @Nullable
    private T defaultValue;
    @Nullable
    private Component tooltip;
    private BooleanSupplier active = () -> true;

    public CycleSetting(Component name, Supplier<List<T>> values, Supplier<T> getter, Consumer<T> setter, Function<T, Component> display) {
        this.name = name;
        this.values = values;
        this.getter = getter;
        this.setter = setter;
        this.display = display;
    }

    public static CycleSetting<Boolean> toggle(Component name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new CycleSetting<>(name, () -> List.of(true, false), getter, setter, value -> value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    public CycleSetting<T> defaultValue(T value) {
        this.defaultValue = value;
        return this;
    }

    public CycleSetting<T> tooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public CycleSetting<T> active(BooleanSupplier active) {
        this.active = active;
        return this;
    }

    @Override
    public Component name() {
        return name;
    }

    @Override
    public List<FormattedCharSequence> tooltip() {
        return tooltip == null ? List.of() : Minecraft.getInstance().font.split(tooltip, 200);
    }

    @Override
    public boolean isDefault() {
        return defaultValue == null || Objects.equals(getter.get(), defaultValue);
    }

    @Override
    public boolean canReset() {
        return defaultValue != null;
    }

    @Override
    public void reset() {
        if (defaultValue != null) setter.accept(defaultValue);
    }

    @Override
    public boolean isActive() {
        return active.getAsBoolean();
    }

    @Override
    public AbstractWidget createWidget(int width) {
        return CycleButton.builder(display)
                .withValues(values.get())
                .withInitialValue(getter.get())
                .displayOnlyValue()
                .create(0, 0, width, 20, name, (button, value) -> setter.accept(value));
    }
}
