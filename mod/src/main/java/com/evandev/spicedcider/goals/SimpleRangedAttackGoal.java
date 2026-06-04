package com.evandev.spicedcider.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class SimpleRangedAttackGoal<T extends Mob> extends Goal {

    protected final T mob;
    protected final BiConsumer<T, LivingEntity> performRangedAttack;
    protected final Predicate<ItemStack> weaponPredicate;
    protected final double speedModifier;
    protected final int attackIntervalMin;
    protected final int attackIntervalMax;
    protected final float attackRadius;
    protected final float attackRadiusSqr;

    protected LivingEntity target;
    protected int attackTime = -1;
    protected int seeTime;

    public SimpleRangedAttackGoal(T mob, Predicate<ItemStack> weaponPredicate, BiConsumer<T, LivingEntity> performRangedAttack, double speedModifier, int attackInterval, float attackRadius) {
        this(mob, weaponPredicate, performRangedAttack, speedModifier, attackInterval, attackInterval, attackRadius);
    }

    public SimpleRangedAttackGoal(T mob, Predicate<ItemStack> weaponPredicate, BiConsumer<T, LivingEntity> performRangedAttack, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
        this.mob = mob;
        this.weaponPredicate = weaponPredicate;
        this.performRangedAttack = performRangedAttack;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackIntervalMax = attackIntervalMax;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isHolding(this.weaponPredicate)) {
            return false;
        }

        LivingEntity currentTarget = this.mob.getTarget();
        if (currentTarget != null && currentTarget.isAlive()) {
            this.target = currentTarget;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.mob.isHolding(this.weaponPredicate)) {
            return false;
        }
        return this.canUse() || !this.mob.getNavigation().isDone();
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        double distanceSqr = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(this.target);

        if (hasLineOfSight) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (distanceSqr <= (double) this.attackRadiusSqr && this.seeTime >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (--this.attackTime == 0) {
            if (!hasLineOfSight) {
                return;
            }

            float distanceRatio = Mth.sqrt((float) distanceSqr) / this.attackRadius;
            this.performRangedAttack.accept(this.mob, this.target);
            this.attackTime = Mth.floor(distanceRatio * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSqr) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
        }
    }
}