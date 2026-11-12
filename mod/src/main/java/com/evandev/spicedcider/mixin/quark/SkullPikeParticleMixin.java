package com.evandev.spicedcider.mixin.quark;

import com.evandev.spicedcider.registry.ModParticleTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("quark")
@Mixin(targets = "org.violetmoon.quark.content.tools.entity.SkullPike", remap = false)
public class SkullPikeParticleMixin {

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private int spicedcider$windlessParticles(ServerLevel level, ParticleOptions particle, double x, double y, double z, int count,
                                              double dx, double dy, double dz, double speed, Operation<Integer> original) {
        ParticleOptions swapped = particle == ParticleTypes.WARPED_SPORE
                ? ModParticleTypes.SKULL_PIKE_SPORE.get()
                : ModParticleTypes.SKULL_PIKE_ASH.get();
        return original.call(level, swapped, x, y, z, count, dx, dy, dz, speed);
    }
}
