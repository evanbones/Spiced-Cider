package com.evandev.spicedcider.mixin.copperagebackport;

import com.github.smallinger.copperagebackport.entity.ai.behavior.TransportItemsBetweenContainers;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.caverns_and_chasms.core.interfaces.ControllableGolem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("copperagebackport")
@IfModLoaded("caverns_and_chasms")
@Mixin(TransportItemsBetweenContainers.class)
public class TransportItemsControlledMixin {

    @Inject(method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$idleWhileControlled(ServerLevel level, PathfinderMob mob, CallbackInfoReturnable<Boolean> cir) {
        if (mob instanceof ControllableGolem golem && golem.isBeingTuningForkControlled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canStillUse(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$stopWhileControlled(ServerLevel level, PathfinderMob mob, long gameTime, CallbackInfoReturnable<Boolean> cir) {
        if (mob instanceof ControllableGolem golem && golem.isBeingTuningForkControlled()) {
            cir.setReturnValue(false);
        }
    }
}
