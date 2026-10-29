package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ShapeWheel;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("clutternomore")
@IfModLoaded("helditemtooltips")
@Mixin(targets = "fuzs.helditemtooltips.client.handler.SelectedItemHandler", remap = false)
public class HeldItemTooltipsShapeWheelMixin {

    @Shadow
    private int remainingHighlightTicks;

    @Inject(method = "onEndClientTick", at = @At("TAIL"))
    private void spicedcider$hideUnderShapeWheel(Minecraft minecraft, CallbackInfo ci) {
        if (ShapeWheel.isShowing()) remainingHighlightTicks = 0;
    }
}
