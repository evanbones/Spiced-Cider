package com.evandev.spicedcider.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class GoldenDandelionParticle extends TextureSheetParticle {
    private GoldenDandelionParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
        this.lifetime = Mth.randomBetweenInclusive(this.random, 60, 120);
        this.hasPhysics = false;
        this.friction = 0.96F;
        this.gravity = -0.01F;
        this.setColor(0.98F, 0.8F, 0.13F);
        this.xd = Mth.nextDouble(this.random, -0.015, 0.015);
        this.yd = Mth.nextDouble(this.random, 0.0, 0.01);
        this.zd = Mth.nextDouble(this.random, -0.015, 0.015);
    }

    @Override
    protected int getLightColor(float partialTick) {
        int light = super.getLightColor(partialTick);
        return (light & 0xFF0000) | 0xC0;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            GoldenDandelionParticle particle = new GoldenDandelionParticle(level, x, y, z);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
