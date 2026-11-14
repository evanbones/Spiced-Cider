package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.interfaces.WindBlown;
import com.evandev.spicedcider.wind.client.ClientWind;
import com.evandev.spicedcider.wind.client.WindParticles;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Particle.class)
public class ParticleWindMixin implements WindBlown {

    @Shadow
    protected double x;

    @Shadow
    protected double y;

    @Shadow
    protected double z;

    @Shadow
    protected boolean onGround;

    @Unique
    private float spicedcider$windResponse;

    @Override
    public void spicedcider$setWindResponse(float response) {
        spicedcider$windResponse = response;
    }

    @WrapMethod(method = "move")
    private void spicedcider$windDrift(double dx, double dy, double dz, Operation<Void> original) {
        if (spicedcider$windResponse > 0.0F && !onGround) {
            float push = WindParticles.push(x, y, z) * spicedcider$windResponse;
            if (push > 0.0F) {
                dx += ClientWind.directionX() * push;
                dz += ClientWind.directionZ() * push;
            }
        }
        original.call(dx, dy, dz);
    }
}
