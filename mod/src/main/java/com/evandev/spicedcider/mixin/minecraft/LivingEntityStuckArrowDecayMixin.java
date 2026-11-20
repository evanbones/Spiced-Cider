package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.stuckarrows.StuckArrows;
import com.evandev.spicedcider.registry.ModAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityStuckArrowDecayMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setArrowCount(I)V"))
    private void spicedcider$keepRecoverableArrows(LivingEntity entity, int count, Operation<Void> original) {
        if (!entity.level().isClientSide && entity.hasData(ModAttachments.STUCK_ARROWS)) {
            StuckArrows.onDecay(entity);
        } else {
            original.call(entity, count);
        }
    }
}
