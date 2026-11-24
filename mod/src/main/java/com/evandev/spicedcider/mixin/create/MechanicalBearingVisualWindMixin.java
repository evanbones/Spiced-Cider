package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.WindmillVisual;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(value = MechanicalBearingBlockEntity.class, remap = false)
public class MechanicalBearingVisualWindMixin {

    @Unique
    private float spicedcider$visualOffset(float partialTicks) {
        Object self = this;
        if (!(self instanceof WindmillVisual visual)) return 0.0F;
        Level level = ((BlockEntity) self).getLevel();
        return level != null && level.isClientSide ? visual.spicedcider$visualOffset(partialTicks) : 0.0F;
    }

    @WrapOperation(method = "applyRotation", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/ControlledContraptionEntity;setAngle(F)V"))
    private void spicedcider$visualAngle(ControlledContraptionEntity entity, float angle, Operation<Void> original) {
        original.call(entity, angle + spicedcider$visualOffset(1.0F));
    }

    @ModifyReturnValue(method = "getInterpolatedAngle", at = @At("RETURN"))
    private float spicedcider$visualInterpolatedAngle(float angle, float partialTicks) {
        return angle + spicedcider$visualOffset(partialTicks);
    }
}
