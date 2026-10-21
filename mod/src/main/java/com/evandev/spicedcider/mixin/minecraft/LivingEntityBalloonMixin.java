package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.entities.balloon.BalloonTethers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityBalloonMixin {

    @WrapOperation(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getGravity()D"
            )
    )
    private double balloonGravity(LivingEntity entity, Operation<Double> original) {
        return BalloonTethers.gravity(entity, original.call(entity));
    }
}
