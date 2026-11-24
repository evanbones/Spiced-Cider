package com.evandev.spicedcider.mixin.offgrid;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Offsets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("offgrid")
@Mixin(LivingEntity.class)
public abstract class LivingEntityOffgridBedMixin {
    @WrapMethod(method = "setPosToBed")
    private void spicedcider$sleepOnOffsetBed(BlockPos pos, Operation<Void> original) {
        original.call(pos);
        LivingEntity entity = (LivingEntity) (Object) this;
        Vec3 offset = Offsets.at(entity.level(), pos);
        if (offset == null) return;
        if (entity.position().distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.6875, pos.getZ() + 0.5) > 1.0E-4) return;
        entity.setPos(entity.getX() + offset.x, entity.getY() + offset.y, entity.getZ() + offset.z);
    }
}
