package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.tips.Tips;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InBedChatScreen.class)
public abstract class InBedChatScreenTipsMixin extends ChatScreen {

    @Shadow
    private Button leaveBedButton;

    private InBedChatScreenTipsMixin(String initial) {
        super(initial);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void spicedcider$hideChatInput(CallbackInfo ci) {
        this.removeWidget(this.input);
        this.setFocused(null);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void spicedcider$renderTips(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ci.cancel();
        this.leaveBedButton.render(graphics, mouseX, mouseY, partialTick);
        int besideButton = this.leaveBedButton.getX() - 16;
        if (besideButton >= 160) {
            Tips.render(graphics, this.font, this.height - 8, besideButton);
        } else {
            Tips.render(graphics, this.font, this.leaveBedButton.getY() - 8, this.width - 16);
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void spicedcider$ignoreTyping(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void spicedcider$openChat(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        String initial = this.minecraft.options.keyChat.matches(keyCode, scanCode) ? ""
                : this.minecraft.options.keyCommand.matches(keyCode, scanCode) ? "/" : null;
        if (initial == null) return;
        this.minecraft.tell(() -> this.minecraft.setScreen(new ChatScreen(initial)));
        cir.setReturnValue(true);
    }
}
