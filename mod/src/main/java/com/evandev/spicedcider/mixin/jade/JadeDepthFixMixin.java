package com.evandev.spicedcider.mixin.jade;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.impl.ui.BoxElement;
import snownee.jade.overlay.OverlayRenderer;

@Mixin(value = OverlayRenderer.class, remap = false)
public class JadeDepthFixMixin {

    @Inject(
            method = "renderOverlay",
            at = @At(value = "INVOKE", target = "Lsnownee/jade/impl/ui/BoxElement;render(Lnet/minecraft/client/gui/GuiGraphics;FFFF)V")
    )
    private static void usefulspyglass$fixDepthPollutionBefore(BoxElement root, GuiGraphics guiGraphics, CallbackInfo ci) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 400);
    }

    @Inject(
            method = "renderOverlay",
            at = @At(value = "INVOKE", target = "Lsnownee/jade/impl/ui/BoxElement;render(Lnet/minecraft/client/gui/GuiGraphics;FFFF)V", shift = At.Shift.AFTER)
    )
    private static void usefulspyglass$fixDepthPollutionAfter(BoxElement root, GuiGraphics guiGraphics, CallbackInfo ci) {
        guiGraphics.pose().popPose();
        RenderSystem.enableDepthTest();
    }
}