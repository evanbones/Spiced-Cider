package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.entities.balloon.BalloonTethers;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public class BoatBalloonMixin {

    @Inject(method = "floatBoat", at = @At("TAIL"))
    private void balloonLift(CallbackInfo ci) {
        BalloonTethers.liftBoat((Boat) (Object) this);
    }
}
