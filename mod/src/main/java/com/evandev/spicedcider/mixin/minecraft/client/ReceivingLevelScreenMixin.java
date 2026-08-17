package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.progress.ProgressRenderer;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReceivingLevelScreen.class)
public abstract class ReceivingLevelScreenMixin extends Screen {

    private ReceivingLevelScreenMixin(Component title) {
        super(title);
    }

    @WrapWithCondition(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
            )
    )
    private boolean spicedcider$shouldRenderReceivingLevelText(GuiGraphics graphics, Font font, Component text, int x, int y, int color) {
        if (!SpicedCiderConfig.CLIENT.oldProgressScreen.get()) {
            return true;
        }

        ProgressRenderer.drawHeaderText(graphics, Component.translatable("gui.spicedcider.screen.level.loading"), this.width);
        ProgressRenderer.drawStageText(graphics, Component.translatable("gui.spicedcider.screen.level.simulate"), this.width);

        return false;
    }
}
