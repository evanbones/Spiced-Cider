package com.evandev.spicedcider.mixin.snowyspirit;

import net.mehvahdjukaar.snowyspirit.client.GlowLightParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlowLightParticle.class)
public abstract class GlowLightParticleMixin extends TextureSheetParticle {

    protected GlowLightParticleMixin(ClientLevel arg, double d, double e, double f) {
        super(arg, d, e, f);
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void injectDefaultSpriteForSodium(ClientLevel arg, double d, double e, double f, SpriteSet sprites, CallbackInfo ci) {
        this.setSprite(sprites.get(0, 3));
    }
}