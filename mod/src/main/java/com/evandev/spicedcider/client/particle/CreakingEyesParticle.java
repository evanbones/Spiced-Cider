package com.evandev.spicedcider.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class CreakingEyesParticle extends TextureSheetParticle {
    public static final int MAX_LIGHT = 7;

    private CreakingEyesParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.quadSize *= 2.0F;
        this.lifetime = 200 + this.random.nextInt(40);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.getMaxLocalRawBrightness(BlockPos.containing(this.x, this.y, this.z)) > MAX_LIGHT) {
            this.remove();
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
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
            CreakingEyesParticle particle = new CreakingEyesParticle(level, x, y, z);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
