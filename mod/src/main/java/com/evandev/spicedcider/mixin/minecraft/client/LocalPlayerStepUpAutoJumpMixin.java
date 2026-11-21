package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerStepUpAutoJumpMixin {

    @ModifyReturnValue(method = "isAutoJumpEnabled", at = @At("RETURN"))
    private boolean spicedcider$stepUpReplacesAutoJump(boolean original) {
        return original && !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.stepUp, false);
    }
}
