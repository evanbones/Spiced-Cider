package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.Blaze$BlazeAttackGoal")
public abstract class BlazeAttackGoalSingleFireballMixin {
    private static final int SPICEDCIDER$CHARGE_TICKS = 30;
    private static final int SPICEDCIDER$COOLDOWN_TICKS = 34;

    private static boolean spicedcider$enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.singleBlazeFireball, false);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=4"))
    private int spicedcider$oneShot(int original) {
        return spicedcider$enabled() ? 2 : original;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=60"))
    private int spicedcider$chargeTime(int original) {
        return spicedcider$enabled() ? SPICEDCIDER$CHARGE_TICKS : original;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int spicedcider$cooldown(int original) {
        return spicedcider$enabled() ? SPICEDCIDER$COOLDOWN_TICKS : original;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;triangle(DD)D"))
    private double spicedcider$aimed(RandomSource random, double mean, double deviation, Operation<Double> original) {
        return spicedcider$enabled() ? mean : original.call(random, mean, deviation);
    }
}
