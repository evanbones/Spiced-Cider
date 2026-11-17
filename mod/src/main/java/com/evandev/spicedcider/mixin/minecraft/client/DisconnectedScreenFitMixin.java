package com.evandev.spicedcider.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenFitMixin extends Screen {

    @Shadow
    @Final
    private Screen parent;

    @Shadow
    @Final
    private DisconnectionDetails details;

    @Unique
    private boolean spicedcider$logged;

    protected DisconnectedScreenFitMixin(Component title) {
        super(title);
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/MultiLineTextWidget;setMaxWidth(I)Lnet/minecraft/client/gui/components/MultiLineTextWidget;"))
    private MultiLineTextWidget spicedcider$fitReason(MultiLineTextWidget widget, int maxWidth, Operation<MultiLineTextWidget> original) {
        if (!this.spicedcider$logged) {
            this.spicedcider$logged = true;
            LogUtils.getLogger().info("Disconnected: {}", this.details.reason().getString());
        }
        int buttons = 1 + (this.details.bugReportLink().isPresent() ? 1 : 0) + (this.details.report().isPresent() ? 1 : 0);
        int rows = Math.max(1, (this.height - 60 - buttons * 24) / this.font.lineHeight);
        return original.call(widget, maxWidth).setMaxRows(rows);
    }

    @ModifyReturnValue(method = "shouldCloseOnEsc", at = @At("RETURN"))
    private boolean spicedcider$closeOnEsc(boolean original) {
        return true;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.minecraft.allowsMultiplayer() ? this.parent : new TitleScreen());
    }
}
