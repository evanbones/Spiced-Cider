package com.evandev.spicedcider.mixin.clutternomore;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("clutternomore")
@Mixin(value = ClutterNoMoreClient.class, remap = false)
public class ControllerShapeHoldMixin {

    @WrapMethod(method = "onControllerShapeKey")
    private static void spicedcider$holdOnController(boolean down, boolean justPressed, boolean justReleased, boolean justTapped, Operation<Void> original) {
        if (justReleased) {
            if (ClutterNoMoreClient.getOverlay() != null) ClutterNoMoreClient.closeOverlay();
            return;
        }
        if (!justPressed || ClutterNoMoreClient.getOverlay() != null) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.screen != null) return;
        InteractionHand hand = ClutterNoMoreClient.shapeHand(minecraft);
        if (hand != null) ClutterNoMoreClientAccessor.spicedcider$setOverlay(new ShapeSwitcherOverlay(minecraft, hand, true));
    }
}
