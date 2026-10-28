package com.evandev.spicedcider.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityFirstPersonEffectParticlesMixin {

    @WrapOperation(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void spicedcider$hideOwnInFirstPerson(Level level, ParticleOptions particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Void> original) {
        Minecraft minecraft = Minecraft.getInstance();
        if (level.isClientSide() && minecraft.getCameraEntity() == (Object) this && minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }
        original.call(level, particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
