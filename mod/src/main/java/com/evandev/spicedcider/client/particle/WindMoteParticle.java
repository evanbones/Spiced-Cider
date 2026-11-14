package com.evandev.spicedcider.client.particle;

import com.evandev.spicedcider.wind.client.ClientWind;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WindMoteParticle extends TextureSheetParticle {
    private static final float SPEED_PER_STRENGTH = 0.22F;
    private static final int FADE_TICKS = 12;

    private final float swayPhase;
    private final float bobPhase;

    private WindMoteParticle(ClientLevel level, double x, double y, double z, float red, float green, float blue) {
        super(level, x, y, z);
        this.quadSize = 0.02F + this.random.nextFloat() * 0.015F;
        this.lifetime = Mth.randomBetweenInclusive(this.random, 100, 180);
        this.hasPhysics = true;
        this.gravity = 0.0F;
        this.alpha = 0.0F;
        this.swayPhase = this.random.nextFloat() * Mth.TWO_PI;
        this.bobPhase = this.random.nextFloat() * Mth.TWO_PI;
        this.setColor(red, green, blue);
        this.xd = 0.0;
        this.yd = 0.01;
        this.zd = 0.0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        if (this.onGround) this.age += 5;

        float strength = ClientWind.exposedStrength(this.x, this.y, this.z);
        float directionX = ClientWind.directionX();
        float directionZ = ClientWind.directionZ();
        float sway = Mth.sin(this.age * 0.09F + this.swayPhase) * 0.02F * ClientWind.turbulence();
        double targetX = directionX * strength * SPEED_PER_STRENGTH - directionZ * sway;
        double targetZ = directionZ * strength * SPEED_PER_STRENGTH + directionX * sway;
        double targetY = Mth.sin(this.age * 0.05F + this.bobPhase) * (0.008F + 0.02F * strength) + 0.004F * strength;
        this.xd += (targetX - this.xd) * 0.08;
        this.yd += (targetY - this.yd) * 0.05;
        this.zd += (targetZ - this.zd) * 0.08;
        this.move(this.xd, this.yd, this.zd);

        int remaining = this.lifetime - this.age;
        this.alpha = Mth.clamp(Math.min(this.age, remaining) / (float) FADE_TICKS, 0.0F, 1.0F) * 0.85F;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double red, double green, double blue) {
            WindMoteParticle particle = new WindMoteParticle(level, x, y, z, (float) red, (float) green, (float) blue);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
