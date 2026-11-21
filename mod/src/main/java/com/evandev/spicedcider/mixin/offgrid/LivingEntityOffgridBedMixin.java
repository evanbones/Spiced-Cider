package com.evandev.spicedcider.mixin.offgrid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Offsets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(LivingEntity.class)
public abstract class LivingEntityOffgridBedMixin {
    @WrapOperation(method = "setPosToBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setPos(DDD)V"))
    private void spicedcider$sleepOnOffsetBed(LivingEntity entity, double x, double y, double z, Operation<Void> original, BlockPos pos) {
        Vec3 offset = Offsets.at(entity.level(), pos);
        if (offset == null) {
            original.call(entity, x, y, z);
            return;
        }
        original.call(entity, x + offset.x, y + offset.y, z + offset.z);
    }
}
