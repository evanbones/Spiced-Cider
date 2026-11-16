package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ShapeWheel;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("clutternomore")
@Mixin(value = ShapeSwitcherOverlay.class, remap = false)
public class ShapeSwitcherOverlayWheelMixin {

    @WrapMethod(method = "render")
    private void spicedcider$renderWheel(GuiGraphics graphics, float partialTick, Operation<Void> original) {
        if (!ShapeWheel.owns((ShapeSwitcherOverlay) (Object) this)) original.call(graphics, partialTick);
    }

    @WrapMethod(method = "tickLook")
    private void spicedcider$noLookSwitching(Operation<Void> original) {
        if (!ShapeWheel.enabled()) original.call();
    }
}
