package com.evandev.spicedcider.mixin.tinytakeoverbackport;

import com.evandev.spicedcider.compat.tinytakeover.GoldenDandelions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("tiny_takeover_backport")
@Mixin(TemptGoal.class)
public abstract class TemptGoalGoldenDandelionMixin {
    @Shadow
    @Final
    protected PathfinderMob mob;

    @ModifyReturnValue(method = "shouldFollow", at = @At("RETURN"))
    private boolean spicedcider$goldenDandelion(boolean original, LivingEntity entity) {
        return original || GoldenDandelions.tempts(this.mob, entity);
    }
}
