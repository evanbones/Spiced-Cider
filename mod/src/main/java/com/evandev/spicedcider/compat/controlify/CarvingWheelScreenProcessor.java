package com.evandev.spicedcider.compat.controlify;

import com.evandev.spicedcider.compat.nomansland.CarvingWheelScreen;
import dev.isxander.controlify.bindings.ControlifyBindings;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import dev.isxander.controlify.virtualmouse.VirtualMouseBehaviour;

public class CarvingWheelScreenProcessor extends ScreenProcessor<CarvingWheelScreen> {
    private static final float STICK_THRESHOLD = 0.5F;
    private static final float STICK_REACH = 100.0F;

    public CarvingWheelScreenProcessor(CarvingWheelScreen screen) {
        super(screen);
    }

    public static void register() {
        ScreenProcessorProvider.registerProvider(CarvingWheelScreen.class, CarvingWheelScreenProcessor::new);
    }

    @Override
    public VirtualMouseBehaviour virtualMouseBehaviour() {
        return VirtualMouseBehaviour.DISABLED;
    }

    @Override
    public void onControllerUpdate(ControllerEntity controller) {
        steer(ControlifyBindings.LOOK_RIGHT.on(controller).analogueNow() - ControlifyBindings.LOOK_LEFT.on(controller).analogueNow(),
                ControlifyBindings.LOOK_DOWN.on(controller).analogueNow() - ControlifyBindings.LOOK_UP.on(controller).analogueNow());
        steer(ControlifyBindings.WALK_RIGHT.on(controller).analogueNow() - ControlifyBindings.WALK_LEFT.on(controller).analogueNow(),
                ControlifyBindings.WALK_BACKWARD.on(controller).analogueNow() - ControlifyBindings.WALK_FORWARD.on(controller).analogueNow());
        if (ControlifyBindings.GUI_PRESS.on(controller).justPressed()) {
            screen.confirm();
        } else if (ControlifyBindings.GUI_BACK.on(controller).justPressed()) {
            playClackSound();
            screen.onClose();
        }
    }

    private void steer(float x, float y) {
        float length = (float) Math.sqrt(x * x + y * y);
        if (length >= STICK_THRESHOLD) screen.point(x / length * STICK_REACH, y / length * STICK_REACH);
    }
}
