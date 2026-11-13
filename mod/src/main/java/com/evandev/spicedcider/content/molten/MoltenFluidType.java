package com.evandev.spicedcider.content.molten;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class MoltenFluidType extends FluidType {
    public MoltenFluidType() {
        super(Properties.create()
                .canSwim(false)
                .canDrown(false)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(false)
                .pathType(PathType.LAVA)
                .adjacentPathType(null)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .lightLevel(15)
                .density(3000)
                .viscosity(6000)
                .temperature(1300));
    }

    @Override
    public double motionScale(Entity entity) {
        return 0.0023333333333333335D;
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        double startY = entity.getY();
        entity.moveRelative(0.02F, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        if (entity.getFluidTypeHeight(this) <= entity.getFluidJumpThreshold()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.5, 0.8F, 0.5));
            entity.setDeltaMovement(entity.getFluidFallingAdjustedMovement(gravity, entity.getDeltaMovement().y <= 0.0, entity.getDeltaMovement()));
        } else {
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));
        }
        if (gravity != 0.0) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -gravity / 4.0, 0.0));
        }
        Vec3 motion = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.isFree(motion.x, motion.y + 0.6F - entity.getY() + startY, motion.z)) {
            entity.setDeltaMovement(motion.x, 0.3F, motion.z);
        }
        return true;
    }
}
