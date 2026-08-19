package com.evandev.spicedcider.mixin.jade;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.impl.ui.BoxElement;
import snownee.jade.overlay.OverlayRenderer;

@Mixin(value = OverlayRenderer.class, remap = false)
public class JadeDepthFixMixin {

    @Inject(method = "renderOverlay", at = @At("HEAD"))
    private static void spicedcider$clearDepthBeforeOverlay(BoxElement root, GuiGraphics guiGraphics, CallbackInfo ci) {
        RenderSystem.clear(256, Minecraft.ON_OSX);
    }

    @Inject(
            method = "renderOverlay",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableDepthTest()V")
    )
    private static void spicedcider$flushJadeOverlay(BoxElement root, GuiGraphics guiGraphics, CallbackInfo ci) {
        guiGraphics.flush();
    }
}