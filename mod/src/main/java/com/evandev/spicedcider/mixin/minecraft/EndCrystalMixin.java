package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.interfaces.IEndCrystalHealer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystal.class)
public abstract class EndCrystalMixin implements IEndCrystalHealer {

    @Unique
    private LivingEntity cider$healingTarget = null;

    @Shadow
    @Nullable
    public abstract BlockPos getBeamTarget();

    @Override
    public LivingEntity cider$getHealingTarget() {
        return this.cider$healingTarget;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void cider$healNearbyEntities(CallbackInfo ci) {
        EndCrystal crystal = (EndCrystal) (Object) this;
        Level level = crystal.level();

        if (this.getBeamTarget() != null) {
            this.cider$healingTarget = null;
            return;
        }

        double range = 12.0D;
        AABB searchBox = crystal.getBoundingBox().inflate(range);

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (entity.isAlive() && entity.getHealth() < entity.getMaxHealth() && !(entity instanceof EnderDragon)) {
                double dist = crystal.distanceToSqr(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }

        this.cider$healingTarget = closest;

        if (closest != null && !level.isClientSide()) {
            if (crystal.tickCount % 10 == 0) {
                closest.heal(1.0F);
            }
        }
    }
}