package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.registry.ModSounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ConsumeGolemGoal extends MeleeAttackGoal {

    public ConsumeGolemGoal(PathfinderMob mob) {
        super(mob, 0.8, true);
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() instanceof StrawGolem && super.canUse();
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (canPerformAttack(target) && target instanceof StrawGolem) {
            resetAttackCooldown();
            mob.playSound(SoundEvents.GENERIC_EAT, 2.0F, 0.5F);
            target.hurt(target.level().damageSources().mobAttack(mob), 0.5F);
            target.playSound(ModSounds.STRAW_GOLEM_SCARED.get());
        }
    }
}
