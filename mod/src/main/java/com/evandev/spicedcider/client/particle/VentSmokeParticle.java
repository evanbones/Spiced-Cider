package com.evandev.spicedcider.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class VentSmokeParticle extends TextureSheetParticle {
    private final float growth;

    private VentSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float shade) {
        super(level, x, y, z);
        this.lifetime = 50 + this.random.nextInt(40);
        this.gravity = -0.0015F;
        this.friction = 0.97F;
        this.xd = xSpeed + (this.random.nextFloat() - 0.5F) * 0.01F;
        this.yd = ySpeed + 0.04F + this.random.nextFloat() * 0.02F;
        this.zd = zSpeed + (this.random.nextFloat() - 0.5F) * 0.01F;
        float tint = shade + (this.random.nextFloat() - 0.5F) * 0.08F;
        this.rCol = tint;
        this.gCol = tint;
        this.bCol = tint;
        this.quadSize = 0.25F + this.random.nextFloat() * 0.15F;
        this.growth = 0.008F + this.random.nextFloat() * 0.006F;
        this.alpha = 0.85F;
    }

    @Override
    public void tick() {
        super.tick();
        this.quadSize += this.growth;
        float remaining = 1.0F - (float) this.age / this.lifetime;
        this.alpha = Math.min(0.85F, remaining * 1.6F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final float shade;

        public Provider(SpriteSet sprites, float shade) {
            this.sprites = sprites;
            this.shade = shade;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            VentSmokeParticle particle = new VentSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.shade);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
