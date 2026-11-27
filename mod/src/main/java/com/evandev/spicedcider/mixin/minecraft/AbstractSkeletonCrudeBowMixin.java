package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonCrudeBowMixin {
    @Unique
    private static final double SKELETON_ARROW_DAMAGE = 0.7;

    @WrapOperation(method = "performRangedAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;shoot(DDDFF)V"))
    private void spicedcider$skeletonArrow(AbstractArrow arrow, double x, double y, double z, float velocity, float inaccuracy, Operation<Void> original) {
        if (arrow.hasData(ModAttachments.CRUDE_ARROW)) arrow.removeData(ModAttachments.CRUDE_ARROW);
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.skeletonArrowNerf, false)) {
            arrow.setBaseDamage(arrow.getBaseDamage() * SKELETON_ARROW_DAMAGE);
        }
        original.call(arrow, x, y, z, velocity, inaccuracy);
    }
}
