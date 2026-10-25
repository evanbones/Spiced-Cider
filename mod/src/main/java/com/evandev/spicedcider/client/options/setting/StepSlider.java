package com.evandev.spicedcider.client.options.setting;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class StepSlider extends AbstractSliderButton {

    private final int min;
    private final int max;
    private final int step;
    private final IntFunction<Component> format;
    private final IntConsumer onChange;
    private final Runnable onRelease;

    public StepSlider(int width, int min, int max, int step, int value, IntFunction<Component> format, IntConsumer onChange, Runnable onRelease) {
        super(0, 0, width, 20, Component.empty(), max == min ? 0 : (double) (Mth.clamp(value, min, max) - min) / (max - min));
        this.min = min;
        this.max = max;
        this.step = step;
        this.format = format;
        this.onChange = onChange;
        this.onRelease = onRelease;
        updateMessage();
    }

    public int intValue() {
        int steps = Math.round((float) (value * (max - min)) / step);
        return Mth.clamp(min + steps * step, min, max);
    }

    @Override
    protected void updateMessage() {
        setMessage(format.apply(intValue()));
    }

    @Override
    protected void applyValue() {
        int snapped = intValue();
        value = max == min ? 0 : (double) (snapped - min) / (max - min);
        onChange.accept(snapped);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        onRelease.run();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
        if (handled) onRelease.run();
        return handled;
    }
}
