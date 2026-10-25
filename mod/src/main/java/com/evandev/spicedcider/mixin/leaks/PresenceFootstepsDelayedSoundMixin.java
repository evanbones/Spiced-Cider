package com.evandev.spicedcider.mixin.leaks;

import com.evandev.spicedcider.util.ClientCacheRegistry;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("presencefootsteps")
@Mixin(targets = "eu.ha3.presencefootsteps.sound.player.DelayedSoundPlayer", remap = false)
public class PresenceFootstepsDelayedSoundMixin {
    @Shadow
    @Final
    private List<?> pending;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$track(CallbackInfo ci) {
        ClientCacheRegistry.track(pending);
    }
}
