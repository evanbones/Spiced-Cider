package com.evandev.spicedcider.mixin.bigbrain;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.PathfinderMob;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("bigbrain")
@Mixin(targets = "tallestegg.bigbrain.common.entity.ai.goals.AnimalFearPlayerGoal", remap = false)
public class AnimalFearGoalDecayMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PathfinderMob;setData(Lnet/neoforged/neoforge/attachment/AttachmentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object spicedcider$decayInTickHandler(PathfinderMob mob, AttachmentType<?> type, Object value, Operation<Object> original) {
        if (SpicedCiderConfig.COMMON.calmerHerdPanic.get()) return null;
        return original.call(mob, type, value);
    }
}
