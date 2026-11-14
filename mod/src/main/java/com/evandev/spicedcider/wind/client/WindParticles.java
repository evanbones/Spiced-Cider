package com.evandev.spicedcider.wind.client;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class WindParticles {
    public static final TagKey<ParticleType<?>> IMMUNE = tag("wind_immune");
    public static final TagKey<ParticleType<?>> LIGHT = tag("wind_light");
    public static final TagKey<ParticleType<?>> SHED = tag("wind_shed");
    private static final float LIGHT_RESPONSE = 1.8F;

    private static volatile Map<ParticleType<?>, Float> responses = Map.of();
    private static volatile Map<ParticleType<?>, Boolean> shed = Map.of();
    private static volatile boolean enabled;
    private static volatile float force;

    public static float response(ParticleType<?> type) {
        return responses.getOrDefault(type, 1.0F);
    }

    public static boolean thinned(ParticleType<?> type) {
        if (!ClientWind.active() || !shed.containsKey(type)) return false;
        float chance = Math.min(1.0F, 0.25F + 1.25F * ClientWind.strength());
        return ThreadLocalRandom.current().nextFloat() >= chance;
    }

    public static float push(double x, double y, double z) {
        if (!enabled || !ClientWind.active()) return 0.0F;
        float strength = ClientWind.exposedStrength(x, y, z);
        return force * strength * (0.6F + 0.4F * strength);
    }

    static void configure(boolean particles, float particleForce) {
        enabled = particles;
        force = particleForce;
    }

    static void reload() {
        Map<ParticleType<?>, Float> next = new IdentityHashMap<>();
        for (Holder<ParticleType<?>> holder : BuiltInRegistries.PARTICLE_TYPE.getTagOrEmpty(LIGHT)) next.put(holder.value(), LIGHT_RESPONSE);
        for (Holder<ParticleType<?>> holder : BuiltInRegistries.PARTICLE_TYPE.getTagOrEmpty(IMMUNE)) next.put(holder.value(), 0.0F);
        Map<ParticleType<?>, Boolean> nextShed = new IdentityHashMap<>();
        for (Holder<ParticleType<?>> holder : BuiltInRegistries.PARTICLE_TYPE.getTagOrEmpty(SHED)) nextShed.put(holder.value(), Boolean.TRUE);
        responses = next;
        shed = nextShed;
    }

    private static TagKey<ParticleType<?>> tag(String path) {
        return TagKey.create(Registries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, path));
    }
}
