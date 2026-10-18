package com.evandev.spicedcider.mixin.chunksfadein;

import com.evandev.spicedcider.compat.voxy.VoxyFadeIn;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@IfModLoaded("chunksfadein")
@Mixin(targets = "com.koteinik.chunksfadein.core.Fader", remap = false)
public abstract class FaderVoxyLodMixin {
    @Shadow
    private float fadeCoeff;

    @Shadow
    @Final
    private int chunkX;

    @Shadow
    @Final
    private int chunkZ;

    @Shadow
    public abstract boolean hasRenderedBefore();

    @Inject(method = "incrementFadeCoeff", at = @At("HEAD"))
    private void spicedcider$skipFadeOverLod(long delta, boolean nearPlayer, CallbackInfoReturnable<Float> cir) {
        if (!hasRenderedBefore() && VoxyFadeIn.isRevealed(chunkX, chunkZ)) fadeCoeff = 1.0F;
    }
}
