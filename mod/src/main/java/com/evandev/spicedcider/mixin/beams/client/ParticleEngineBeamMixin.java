package com.evandev.spicedcider.mixin.beams.client;

import com.evandev.spicedcider.compat.beams.client.BeamParticles;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("beams")
@Mixin(ParticleEngine.class)
public class ParticleEngineBeamMixin {
    @Shadow
    protected ClientLevel level;

    @Inject(method = "addBlockHitEffects(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void spicedcider$beamHitEffects(BlockPos pos, BlockHitResult target, CallbackInfo ci) {
        if (this.level != null && BeamParticles.replaceHitEffects(this.level, (ParticleEngine) (Object) this)) ci.cancel();
    }
}
