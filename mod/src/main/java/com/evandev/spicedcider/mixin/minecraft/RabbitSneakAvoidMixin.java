package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.animal.Rabbit$RabbitAvoidEntityGoal")
public abstract class RabbitSneakAvoidMixin<T extends LivingEntity> extends AvoidEntityGoal<T> {

    public RabbitSneakAvoidMixin(PathfinderMob mob, Class<T> avoidClass, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier) {
        super(mob, avoidClass, maxDistance, walkSpeedModifier, sprintSpeedModifier);
    }

    @ModifyReturnValue(method = "canUse", at = @At("RETURN"))
    private boolean spicedcider$ignoreSneakingPlayers(boolean original) {
        return original && !(this.toAvoid instanceof Player player && player.isShiftKeyDown() && SpicedCiderConfig.COMMON.sneakUpOnRabbits.get());
    }
}
