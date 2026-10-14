package com.evandev.spicedcider.mixin.copperagebackport;

import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.entity.ai.behavior.PressRandomCopperButton;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.caverns_and_chasms.core.interfaces.ControllableGolem;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("copperagebackport")
@IfModLoaded("caverns_and_chasms")
@Mixin(PressRandomCopperButton.class)
public class PressCopperButtonControlledMixin {

    @Inject(method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lcom/github/smallinger/copperagebackport/entity/CopperGolemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$idleWhileControlled(ServerLevel level, CopperGolemEntity golem, CallbackInfoReturnable<Boolean> cir) {
        if (golem instanceof ControllableGolem controllable && controllable.isBeingTuningForkControlled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canStillUse(Lnet/minecraft/server/level/ServerLevel;Lcom/github/smallinger/copperagebackport/entity/CopperGolemEntity;J)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$stopWhileControlled(ServerLevel level, CopperGolemEntity golem, long gameTime, CallbackInfoReturnable<Boolean> cir) {
        if (golem instanceof ControllableGolem controllable && controllable.isBeingTuningForkControlled()) {
            cir.setReturnValue(false);
        }
    }
}
