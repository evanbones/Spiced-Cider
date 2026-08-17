package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.progress.OldProgressScreen;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.sounds.SoundManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientLevel level;

    @Shadow
    @Nullable
    public Screen screen;

    @Shadow
    public abstract RenderBuffers renderBuffers();

    @ModifyArg(
            method = "updateTitle",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;setTitle(Ljava/lang/String;)V"
            )
    )
    private String spicedcider$modifyWindowTitle(String title) {
        if (!SpicedCiderConfig.CLIENT.customWindowTitle.get()) {
            return title;
        }

        String version = SharedConstants.getCurrentVersion().getName();
        String format = SpicedCiderConfig.CLIENT.windowTitleFormat.get();
        String result = format.replace("%v", version);

        if (result.length() > 100) {
            result = result.substring(0, 100);
        }

        return result;
    }

    @Inject(
            method = "runTick",
            at = @At(
                    shift = At.Shift.BEFORE,
                    ordinal = 1,
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;setErrorSection(Ljava/lang/String;)V"
            )
    )
    private void spicedcider$onRunTick(boolean renderLevel, CallbackInfo callback) {
        if (SpicedCiderConfig.CLIENT.oldProgressScreen.get() && !renderLevel) {
            if (this.screen instanceof OldProgressScreen progressScreen) {
                Minecraft minecraft = Minecraft.getInstance();
                GuiGraphics graphics = new GuiGraphics(minecraft, this.renderBuffers().bufferSource());
                double mouseX = minecraft.mouseHandler.xpos() * (double) minecraft.getWindow().getGuiScaledWidth() / (double) minecraft.getWindow().getWidth();
                double mouseY = minecraft.mouseHandler.ypos() * (double) minecraft.getWindow().getGuiScaledHeight() / (double) minecraft.getWindow().getHeight();

                progressScreen.tick();
                progressScreen.render(graphics, (int) mouseX, (int) mouseY, minecraft.getTimer().getGameTimeDeltaPartialTick(true));
            }
        }
    }

    @Inject(
            method = "setLevel",
            at = @At("HEAD")
    )
    private void spicedcider$onSetLevel(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo callback) {
        if (this.level != null) {
            OldProgressScreen.PREVIOUS_DIMENSION = this.level.dimension();
        }
        OldProgressScreen.CURRENT_DIMENSION = level.dimension();
    }

    @Inject(
            method = "disconnect()V",
            at = @At("TAIL")
    )
    private void spicedcider$onDisconnect(CallbackInfo callback) {
        OldProgressScreen.PREVIOUS_DIMENSION = null;
        OldProgressScreen.CURRENT_DIMENSION = null;
    }
}
