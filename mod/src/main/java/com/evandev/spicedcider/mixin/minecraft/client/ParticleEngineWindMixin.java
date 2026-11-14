package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.interfaces.WindBlown;
import com.evandev.spicedcider.wind.client.WindParticles;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ParticleEngine.class)
public class ParticleEngineWindMixin {

    @WrapMethod(method = "createParticle")
    private Particle spicedcider$windResponse(ParticleOptions options, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Particle> original) {
        if (WindParticles.thinned(options.getType())) return null;
        Particle particle = original.call(options, x, y, z, xSpeed, ySpeed, zSpeed);
        if (particle != null) ((WindBlown) particle).spicedcider$setWindResponse(WindParticles.response(options.getType()));
        return particle;
    }
}
