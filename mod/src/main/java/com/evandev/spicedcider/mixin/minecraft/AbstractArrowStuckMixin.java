package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.stuckarrows.StuckArrows;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public class AbstractArrowStuckMixin {

    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setArrowCount(I)V"))
    private void spicedcider$trackStuckArrow(LivingEntity entity, int count, Operation<Void> original) {
        if (StuckArrows.tracks(entity)) {
            StuckArrows.onStuck(entity, (AbstractArrow) (Object) this);
        } else {
            original.call(entity, count);
        }
    }
}
