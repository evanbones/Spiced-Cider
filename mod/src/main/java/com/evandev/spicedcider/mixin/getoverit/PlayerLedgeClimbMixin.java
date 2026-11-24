package com.evandev.spicedcider.mixin.getoverit;

import com.evandev.spicedcider.compat.getoverit.LedgeClimb;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("getoverit")
@Mixin(LocalPlayer.class)
public abstract class PlayerLedgeClimbMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void spicedcider$trackLedgeClimbInput(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        LedgeClimb.tick(player, player.input.jumping);
    }
}
