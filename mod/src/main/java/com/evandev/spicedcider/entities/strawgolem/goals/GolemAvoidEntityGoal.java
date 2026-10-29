package com.evandev.spicedcider.entities.strawgolem.goals;

import com.evandev.spicedcider.entities.strawgolem.StrawGolem;
import com.evandev.spicedcider.registry.ModSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

import java.util.function.Predicate;

public class GolemAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    public GolemAvoidEntityGoal(StrawGolem golem, Class<T> avoidClass, Predicate<Mob> avoid, float maxDistance, double walkSpeed, double sprintSpeed, Predicate<LivingEntity> selector) {
        super(golem, avoidClass, living -> living instanceof Mob mob && avoid.test(mob), maxDistance, walkSpeed, sprintSpeed, selector);
    }

    @Override
    public void start() {
        super.start();
        mob.playSound(ModSounds.STRAW_GOLEM_SCARED.get());
    }
}
