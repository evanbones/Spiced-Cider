package com.evandev.spicedcider.mixin.bigbrain;

import com.evandev.spicedcider.compat.bigbrain.HerdFearDecay;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@IfModLoaded("bigbrain")
@Mixin(targets = "tallestegg.bigbrain.BigBrainEvents", remap = false)
public class HerdPanicSpeedMixin {

    private static final String FEAR_GOAL_INIT = "Ltallestegg/bigbrain/common/entity/ai/goals/AnimalFearPlayerGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;Ljava/lang/Class;FDD)V";

    @ModifyArg(method = "onEntityJoin", at = @At(value = "INVOKE", target = FEAR_GOAL_INIT), index = 3)
    private static double spicedcider$calmerWalkSpeed(double speed) {
        return SpicedCiderConfig.COMMON.calmerHerdPanic.get() ? 1.2 : speed;
    }

    @ModifyArg(method = "onEntityJoin", at = @At(value = "INVOKE", target = FEAR_GOAL_INIT), index = 4)
    private static double spicedcider$calmerSprintSpeed(double speed) {
        return SpicedCiderConfig.COMMON.calmerHerdPanic.get() ? 1.5 : speed;
    }

    @ModifyConstant(method = "onHit", constant = @Constant(intValue = 600))
    private static int spicedcider$shorterFear(int ticks) {
        return SpicedCiderConfig.COMMON.calmerHerdPanic.get() ? HerdFearDecay.FEAR_TICKS : ticks;
    }
}
