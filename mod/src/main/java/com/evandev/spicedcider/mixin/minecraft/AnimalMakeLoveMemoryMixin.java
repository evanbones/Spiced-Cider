package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalMakeLove.class)
public abstract class AnimalMakeLoveMemoryMixin {

    @Inject(method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/Animal;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$requireVisibleEntities(ServerLevel level, Animal owner, CallbackInfoReturnable<Boolean> cir) {
        if (!owner.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)) {
            cir.setReturnValue(false);
        }
    }
}
