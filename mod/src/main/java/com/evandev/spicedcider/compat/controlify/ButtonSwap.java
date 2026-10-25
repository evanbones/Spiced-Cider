package com.evandev.spicedcider.compat.controlify;

import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.bindings.input.AxisInput;
import dev.isxander.controlify.bindings.input.ButtonInput;
import dev.isxander.controlify.bindings.input.Input;
import dev.isxander.controlify.controller.input.GamepadInputs;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Map;

public enum ButtonSwap {
    SHOULDERS("swap_shoulders", Map.of(
            new ButtonInput(GamepadInputs.LEFT_SHOULDER_BUTTON), new AxisInput(GamepadInputs.LEFT_TRIGGER_AXIS),
            new ButtonInput(GamepadInputs.RIGHT_SHOULDER_BUTTON), new AxisInput(GamepadInputs.RIGHT_TRIGGER_AXIS))),
    FACE_AB("swap_ab", Map.of(
            new ButtonInput(GamepadInputs.SOUTH_BUTTON), new ButtonInput(GamepadInputs.EAST_BUTTON))),
    FACE_XY("swap_xy", Map.of(
            new ButtonInput(GamepadInputs.WEST_BUTTON), new ButtonInput(GamepadInputs.NORTH_BUTTON)));

    private final String key;
    private final Map<Input, Input> pairs;

    ButtonSwap(String key, Map<Input, Input> pairs) {
        this.key = key;
        this.pairs = pairs;
    }

    public Component title() {
        return Component.translatable("spicedcider.options.controller." + key);
    }

    public Component tooltip() {
        return Component.translatable("spicedcider.options.controller." + key + ".tooltip");
    }

    private Input partner(Input input) {
        Input forward = pairs.get(input);
        if (forward != null) return forward;
        for (Map.Entry<Input, Input> pair : pairs.entrySet()) {
            if (pair.getValue().equals(input)) return pair.getKey();
        }
        return null;
    }

    public boolean isSwapped(Collection<InputBinding> bindings) {
        int swapped = 0;
        int relevant = 0;
        for (InputBinding binding : bindings) {
            Input partner = partner(binding.defaultInput());
            if (partner == null) continue;
            relevant++;
            if (binding.boundInput().equals(partner)) swapped++;
        }
        return relevant > 0 && swapped * 2 > relevant;
    }

    public void apply(Collection<InputBinding> bindings) {
        for (InputBinding binding : bindings) {
            Input partner = partner(binding.boundInput());
            if (partner != null) binding.setBoundInput(partner);
        }
    }
}
