package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ShapeWheel;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("clutternomore")
@Mixin(Gui.class)
public class GuiShapeWheelMixin {

    @WrapMethod(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V")
    private void spicedcider$hideItemNameUnderShapeWheel(GuiGraphics graphics, int yShift, Operation<Void> original) {
        if (!ShapeWheel.isShowing()) original.call(graphics, yShift);
    }

    @WrapMethod(method = "renderCrosshair")
    private void spicedcider$hideCrosshairUnderShapeWheel(GuiGraphics graphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (!ShapeWheel.isShowing()) original.call(graphics, deltaTracker);
    }
}
