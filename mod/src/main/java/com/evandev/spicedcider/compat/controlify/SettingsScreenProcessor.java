package com.evandev.spicedcider.compat.controlify;

import com.evandev.spicedcider.client.options.SettingsScreen;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.bindings.input.AxisInput;
import dev.isxander.controlify.bindings.input.ButtonInput;
import dev.isxander.controlify.bindings.input.HatInput;
import dev.isxander.controlify.bindings.input.Input;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.controller.input.ControllerStateView;
import dev.isxander.controlify.controller.input.HatState;
import dev.isxander.controlify.controller.input.InputComponent;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class SettingsScreenProcessor extends ScreenProcessor<SettingsScreen> {

    private static final int CAPTURE_DELAY = 5;

    private int captureTicks;

    public SettingsScreenProcessor(SettingsScreen screen) {
        super(screen);
    }

    @Override
    public VirtualMouseBehaviour virtualMouseBehaviour() {
        return VirtualMouseBehaviour.ENABLED;
    }

    @Override
    public void onControllerUpdate(ControllerEntity controller) {
        if (!(screen.captureToken() instanceof InputBinding binding)) {
            captureTicks = 0;
            super.onControllerUpdate(controller);
            return;
        }
        if (++captureTicks < CAPTURE_DELAY) return;
        controller.input().flatMap(SettingsScreenProcessor::pressedInput).ifPresent(input -> {
            binding.setBoundInput(input);
            ControlifySettings.save();
            screen.endCapture();
            captureTicks = 0;
        });
    }

    private static Optional<Input> pressedInput(InputComponent input) {
        ControllerStateView now = input.stateNow();
        ControllerStateView then = input.stateThen();
        for (ResourceLocation button : now.getButtons()) {
            if (now.isButtonDown(button) && !then.isButtonDown(button)) return Optional.of(new ButtonInput(button));
        }
        for (ResourceLocation axis : now.getAxes()) {
            if (now.getAxisState(axis) > 0.5F && then.getAxisState(axis) <= 0.5F) return Optional.of(new AxisInput(axis));
        }
        for (ResourceLocation hat : now.getHats()) {
            HatState state = now.getHatState(hat);
            if (state != HatState.CENTERED && then.getHatState(hat) == HatState.CENTERED) return Optional.of(new HatInput(hat, state));
        }
        return Optional.empty();
    }
}
