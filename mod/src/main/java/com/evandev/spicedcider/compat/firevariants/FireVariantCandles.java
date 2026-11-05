package com.evandev.spicedcider.compat.firevariants;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class FireVariantCandles {
    @Nullable
    public static ParticleType<?> particle(Block block) {
        if (!FireVariantCompat.buzzierBees) return null;
        if (BuzzierBeesFireBlocks.sulfurCandle != null && block == BuzzierBeesFireBlocks.sulfurCandle.get()) {
            return BuiltInRegistries.PARTICLE_TYPE.get(BuzzierBeesFireBlocks.SULFUR_FLAME);
        }
        if (BuzzierBeesFireBlocks.glowlightCandle != null && block == BuzzierBeesFireBlocks.glowlightCandle.get()) {
            return BuiltInRegistries.PARTICLE_TYPE.get(BuzzierBeesFireBlocks.GLOWLIGHT_FLAME);
        }
        return null;
    }
}
