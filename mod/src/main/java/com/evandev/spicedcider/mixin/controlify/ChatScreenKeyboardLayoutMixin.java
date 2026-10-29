package com.evandev.spicedcider.mixin.controlify;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.screenop.keyboard.ChatKeyboardDucky;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("controlify")
@Mixin(value = ChatScreen.class, priority = 1100)
public abstract class ChatScreenKeyboardLayoutMixin extends Screen {

    @Shadow
    protected EditBox input;

    protected ChatScreenKeyboardLayoutMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void spicedcider$keepInputAboveKeyboard(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int shift = (int) (height * ChatKeyboardDucky.getKeyboardShiftAmount((ChatScreen) (Object) this));
        if (shift > 0 && input.getY() > height - shift - 12) input.setY(input.getY() - shift);
    }
}
