package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ShapeWheel;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@IfModLoaded("clutternomore")
@Mixin(value = ShapeSwitcherOverlay.class, remap = false)
public class ShapeSwitcherOverlayWheelMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private InteractionHand hand;

    @WrapMethod(method = "render")
    private void spicedcider$renderWheel(GuiGraphics graphics, float partialTick, Operation<Void> original) {
        if (minecraft.player == null || !ShapeWheel.render((ShapeSwitcherOverlay) (Object) this, graphics, minecraft.player.getItemInHand(hand))) {
            original.call(graphics, partialTick);
        }
    }

    @WrapMethod(method = "tickLook")
    private void spicedcider$noLookSwitching(Operation<Void> original) {
    }
}
