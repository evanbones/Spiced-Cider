package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.goals.RangedWebAttackGoal;
import com.evandev.spicedcider.interfaces.ITrapsTarget;
import com.evandev.spicedcider.interfaces.IWebShooter;
import com.evandev.spicedcider.registry.ModSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Spider.class)
public abstract class SpiderEntityMixin extends Monster implements IWebShooter {

    @Unique
    private static final EntityDataAccessor<Boolean> cider$WEBSHOOTING = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BOOLEAN);

    @Unique
    public int cider$targetTrappedCounter = 0;

    @Unique
    private RangedWebAttackGoal<?> cider$rangedWebAttackGoal;

    @Unique
    private LeapAtTargetGoal cider$leapAtTargetGoal;

    @Unique
    private MeleeAttackGoal cider$meleeAttackGoal;

    protected SpiderEntityMixin(EntityType<? extends Monster> type, Level worldIn) {
        super(type, worldIn);
    }

    @Inject(at = @At("TAIL"), method = "registerGoals")
    private void registerGoals(CallbackInfo callbackInfo) {
        this.goalSelector
                .getAvailableGoals()
                .stream()
                .filter(pg -> pg.getPriority() == 4 && pg.getGoal() instanceof MeleeAttackGoal)
                .findFirst()
                .ifPresent(pg -> this.cider$meleeAttackGoal = (MeleeAttackGoal) pg.getGoal());

        this.goalSelector
                .getAvailableGoals()
                .stream()
                .filter(pg -> pg.getPriority() == 3 && pg.getGoal() instanceof LeapAtTargetGoal)
                .findFirst()
                .ifPresent(pg -> this.cider$leapAtTargetGoal = (LeapAtTargetGoal) pg.getGoal());

        this.cider$rangedWebAttackGoal = new RangedWebAttackGoal<>(this, 1.0D, 60, 20.0F);
    }

    @Inject(at = @At("RETURN"), method = "defineSynchedData")
    private void registerData(SynchedEntityData.Builder builder, CallbackInfo callbackInfo) {
        builder.define(cider$WEBSHOOTING, false);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.getType() != EntityType.CAVE_SPIDER) {
            this.cider$reassessAttackGoals();
        }
    }

    @Unique
    private void cider$reassessAttackGoals() {
        LivingEntity target = this.getTarget();
        if (this.cider$meleeAttackGoal != null
                && this.cider$rangedWebAttackGoal != null
                && target != null) {
            if (!this.cider$isTargetTrapped()) {
                this.goalSelector.removeGoal(this.cider$meleeAttackGoal);
                if (this.cider$leapAtTargetGoal != null) {
                    this.goalSelector.removeGoal(this.cider$leapAtTargetGoal);
                }
                this.goalSelector.addGoal(4, this.cider$rangedWebAttackGoal);
            } else {
                this.goalSelector.removeGoal(this.cider$rangedWebAttackGoal);
                if (this.cider$leapAtTargetGoal != null) {
                    this.goalSelector.addGoal(3, this.cider$leapAtTargetGoal);
                }
                this.goalSelector.addGoal(4, this.cider$meleeAttackGoal);
            }
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.cider$targetTrappedCounter > 0) {
            this.cider$targetTrappedCounter--;
        }
    }

    @Override
    public void cider$setTargetTrapped(boolean trapped, boolean notifyOthers) {
        TargetingConditions spiderTargeting = TargetingConditions.forCombat().range(10.0D).ignoreInvisibilityTesting();

        if (notifyOthers) {
            List<Spider> spiders = this.level().getNearbyEntities(Spider.class, spiderTargeting, this, this.getBoundingBox().inflate(10.0D));

            for (Spider spider : spiders) {
                if (spider instanceof ITrapsTarget && this.getTarget() != null && spider.getTarget() != null && spider.getTarget() == this.getTarget()) {
                    ((ITrapsTarget) spider).cider$setTargetTrapped(trapped, false);
                }
            }
        }

        if (trapped) {
            this.cider$targetTrappedCounter = 20;
        } else {
            this.cider$targetTrappedCounter = 0;
        }
    }

    @Override
    public void cider$setTargetTrappedCounter(int value) {
        this.cider$targetTrappedCounter = value;
    }

    @Override
    public boolean cider$isTargetTrapped() {
        return this.cider$targetTrappedCounter > 0;
    }

    @Override
    public boolean cider$isWebShooting() {
        return this.entityData.get(cider$WEBSHOOTING);
    }

    @Override
    public void cider$setWebShooting(boolean webShooting) {
        this.playSound(ModSounds.SPIDER_PREPARE_SHOOT.get(), this.getSoundVolume(), this.getVoicePitch());
        this.entityData.set(cider$WEBSHOOTING, webShooting);
    }

}