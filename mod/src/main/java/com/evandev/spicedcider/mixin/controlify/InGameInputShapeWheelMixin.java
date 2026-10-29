package com.evandev.spicedcider.mixin.controlify;

import com.evandev.spicedcider.compat.clutternomore.ShapeWheel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.ingame.InGameInputHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("controlify")
@IfModLoaded("clutternomore")
@Mixin(value = InGameInputHandler.class, remap = false)
public class InGameInputShapeWheelMixin {

    @Shadow
    private double lookInputX;

    @Shadow
    private double lookInputY;

    @WrapOperation(method = "processPlayerLook", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"))
    private void spicedcider$steerShapeWheel(LocalPlayer player, double yaw, double pitch, Operation<Void> original) {
        if (!ShapeWheel.onStick(lookInputX, lookInputY)) original.call(player, yaw, pitch);
    }
}
