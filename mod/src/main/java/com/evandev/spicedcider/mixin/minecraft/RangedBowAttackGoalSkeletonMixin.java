package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.entity.skeleton.RangedSkeletons;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedBowAttackGoal.class)
public abstract class RangedBowAttackGoalSkeletonMixin {
    private static final double SPICEDCIDER$DISTANCE_SCALE = 0.5;

    @Shadow @Final private Mob mob;
    @Shadow @Final private float attackRadiusSqr;
    @Shadow private int strafingTime;
    @Shadow private int attackIntervalMin;

    @Inject(method = "tick", at = @At("HEAD"))
    private void spicedcider$noStrafing(CallbackInfo ci) {
        if (!RangedSkeletons.applies(this.mob)) return;
        this.strafingTime = Integer.MIN_VALUE;
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;
        AbstractSkeletonAccessor skeleton = (AbstractSkeletonAccessor) this.mob;
        double interval = this.mob.level().getDifficulty() == Difficulty.HARD
                ? skeleton.spicedcider$getHardAttackInterval() : skeleton.spicedcider$getAttackInterval();
        double distance = Math.min(this.mob.distanceToSqr(target) / this.attackRadiusSqr, 1.0);
        double eased = 1.0 - (1.0 - distance) * (1.0 - distance);
        this.attackIntervalMin = (int) (interval * (1.0 - SPICEDCIDER$DISTANCE_SCALE) + interval * SPICEDCIDER$DISTANCE_SCALE * eased);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(Lnet/minecraft/world/entity/Entity;D)Z"))
    private boolean spicedcider$holdPositionInRange(PathNavigation navigation, Entity target, double speed, Operation<Boolean> original) {
        if (RangedSkeletons.applies(this.mob) && this.mob.distanceToSqr(target) <= this.attackRadiusSqr
                && this.mob.getSensing().hasLineOfSight(target)) {
            navigation.stop();
            return false;
        }
        return original.call(navigation, target, speed);
    }
}
