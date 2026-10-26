package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CLEAVER_SWEEP = PARTICLE_TYPES.register("cleaver_sweep",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WHITE_VENT_SMOKE = PARTICLE_TYPES.register("white_vent_smoke",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BLACK_VENT_SMOKE = PARTICLE_TYPES.register("black_vent_smoke",
            () -> new SimpleParticleType(true));
}
