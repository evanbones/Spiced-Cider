package com.evandev.spicedcider.compat.controlify;

import com.mojang.blaze3d.platform.Window;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.screenop.ComponentProcessor;
import dev.isxander.controlify.screenop.ScreenProcessor;
import dev.isxander.controlify.screenop.keyboard.ComponentKeyboardBehaviour;
import dev.isxander.controlify.screenop.keyboard.InputTarget;
import dev.isxander.controlify.screenop.keyboard.KeyboardLayouts;
import dev.isxander.controlify.screenop.keyboard.KeyboardOverlayScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import org.lwjgl.glfw.GLFW;

public class TextWidgetKeyboardProcessor implements ComponentProcessor {
    private final AbstractWidget widget;

    public TextWidgetKeyboardProcessor(AbstractWidget widget) {
        this.widget = widget;
    }

    @Override
    public ComponentKeyboardBehaviour getKeyboardBehaviour(ScreenProcessor<?> screen, ControllerEntity controller) {
        Window window = Minecraft.getInstance().getWindow();
        KeyboardOverlayScreen.KeyboardPositioner positioner = KeyboardOverlayScreen.aboveOrBelowWidgetPositioner(
                (int) (window.getGuiScaledWidth() * 0.8F), (int) (window.getGuiScaledHeight() * 0.4F), 1, widget::getRectangle);
        return new ComponentKeyboardBehaviour.Handled(KeyboardLayouts.full(), new Target(widget), positioner);
    }

    private record Target(AbstractWidget widget) implements InputTarget {
        @Override
        public boolean supportsCharInput() {
            return true;
        }

        @Override
        public boolean acceptChar(char codePoint, int modifiers) {
            widget.charTyped(codePoint, modifiers);
            return true;
        }

        @Override
        public boolean supportsKeyCodeInput() {
            return true;
        }

        @Override
        public boolean acceptKeyCode(int keyCode, int scanCode, int modifiers) {
            widget.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }

        @Override
        public boolean supportsCursorMovement() {
            return true;
        }

        @Override
        public boolean moveCursor(int amount) {
            int key = amount < 0 ? GLFW.GLFW_KEY_LEFT : GLFW.GLFW_KEY_RIGHT;
            for (int step = 0; step < Math.abs(amount); step++) widget.keyPressed(key, 0, 0);
            return true;
        }
    }
}
