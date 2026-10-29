package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.compat.clutternomore.ShapeWheel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("clutternomore")
@Mixin(MouseHandler.class)
public class MouseHandlerShapeWheelMixin {

    @WrapOperation(method = "turnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void spicedcider$steerShapeWheel(LocalPlayer player, double yaw, double pitch, Operation<Void> original) {
        if (!ShapeWheel.onMouseTurn(yaw, pitch)) original.call(player, yaw, pitch);
    }
}
