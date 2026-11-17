package com.evandev.spicedcider.entities.sporal;

import com.evandev.spicedcider.registry.ModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;

public class SporalSupportGoal extends Goal {
    private static final int BUFF_DURATION = 200;
    private static final int REFRESH_THRESHOLD = 40;
    private static final int BESTOW_DELAY = 10;
    private static final double FOLLOW_DISTANCE = 3.0;
    private static final double BESTOW_DISTANCE = 6.0;
    private static final double DASH_DISTANCE = 8.0;

    private final Sporal sporal;
    private final double range;
    private final double speed;
    @Nullable
    private LivingEntity candidate;
    private int bestowTicks = -1;
    private int repathDelay;

    public SporalSupportGoal(Sporal sporal, double range, double speed) {
        this.sporal = sporal;
        this.range = range;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.sporal.getRandom().nextInt(reducedTickDelay(10)) != 0) {
            return false;
        }

        this.candidate = this.findCandidate();
        return this.candidate != null;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity supported = this.sporal.getSupported();
        double leash = this.range * 1.5;
        return supported != null
                && supported.isAlive()
                && !supported.isRemoved()
                && this.sporal.distanceToSqr(supported) <= leash * leash;
    }

    @Override
    public void start() {
        this.sporal.setSupported(this.candidate);
        this.candidate = null;
        this.bestowTicks = -1;
        this.repathDelay = 0;
    }

    @Override
    public void stop() {
        this.sporal.setSupported(null);
        this.sporal.getNavigation().stop();
        this.bestowTicks = -1;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity supported = this.sporal.getSupported();
        if (supported == null) return;

        this.sporal.getLookControl().setLookAt(supported, 30.0F, 30.0F);
        double distanceSqr = this.sporal.distanceToSqr(supported);

        if (distanceSqr > DASH_DISTANCE * DASH_DISTANCE && this.sporal.canDash()) {
            this.sporal.dashTowards(supported.position());
        } else if (distanceSqr > FOLLOW_DISTANCE * FOLLOW_DISTANCE) {
            if (--this.repathDelay <= 0) {
                this.repathDelay = this.adjustedTickDelay(10);
                this.sporal.getNavigation().moveTo(supported, this.speed);
            }
        } else {
            this.sporal.getNavigation().stop();
        }

        if (this.bestowTicks >= 0) {
            if (this.bestowTicks-- == 0) {
                this.bestow(supported);
            }
        } else if (needsBuff(supported) && distanceSqr <= BESTOW_DISTANCE * BESTOW_DISTANCE && this.sporal.hasLineOfSight(supported)) {
            this.bestowTicks = BESTOW_DELAY;
            this.sporal.level().broadcastEntityEvent(this.sporal, Sporal.BESTOW_EVENT);
        }
    }

    private void bestow(LivingEntity supported) {
        supported.addEffect(new MobEffectInstance(ModMobEffects.SPORAL_SPEED, BUFF_DURATION, 0, false, true, true), this.sporal);
        supported.addEffect(new MobEffectInstance(ModMobEffects.SPORAL_STRENGTH, BUFF_DURATION, 0, false, true, true), this.sporal);
    }

    private static boolean needsBuff(LivingEntity entity) {
        return isRunningOut(entity, ModMobEffects.SPORAL_SPEED) || isRunningOut(entity, ModMobEffects.SPORAL_STRENGTH);
    }

    private static boolean isRunningOut(LivingEntity entity, Holder<MobEffect> effect) {
        MobEffectInstance instance = entity.getEffect(effect);
        return instance == null || instance.getDuration() < REFRESH_THRESHOLD;
    }

    @Nullable
    private LivingEntity findCandidate() {
        return this.sporal.level().getEntitiesOfClass(LivingEntity.class, this.sporal.getBoundingBox().inflate(this.range), this::isValidCandidate)
                .stream()
                .min(Comparator.comparingDouble(this.sporal::distanceToSqr))
                .orElse(null);
    }

    private boolean isValidCandidate(LivingEntity entity) {
        return entity instanceof Enemy
                && !(entity instanceof Sporal)
                && entity.isAlive()
                && this.sporal.hasLineOfSight(entity);
    }
}
