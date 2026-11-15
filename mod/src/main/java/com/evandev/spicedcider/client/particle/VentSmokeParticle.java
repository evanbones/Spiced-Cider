package com.evandev.spicedcider.client.particle;

import com.evandev.spicedcider.interfaces.WindBlown;
import com.evandev.spicedcider.wind.client.ClientWind;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class VentSmokeParticle extends TextureSheetParticle {
    public static final ParticleRenderType MERGED_PLUME = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(@NotNull Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public boolean isTranslucent() {
            return false;
        }

        @Override
        public String toString() {
            return "SPICEDCIDER_VENT_PLUME";
        }
    };

    private static final float WATER_TURBULENCE = 0.55F;
    private static final float AIR_WIND_RESPONSE = 1.25F;
    private static final float HOLD_UNTIL = 0.72F;

    private final double sourceY;
    private final double seedX;
    private final double seedZ;
    private final double seedPhase;
    private final float initialSize;
    private final float targetSize;
    private final float initialAlpha;
    private final float shade;

    private VentSmokeParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float shade) {
        super(level, x, y, z);
        this.sourceY = y;
        this.lifetime = 220 + this.random.nextInt(100);
        this.gravity = -0.0016F;
        this.friction = 0.97F;
        this.xd = xSpeed + (this.random.nextFloat() - 0.5F) * 0.01F;
        this.yd = ySpeed + 0.035F + this.random.nextFloat() * 0.02F + (this.random.nextFloat() - 0.5F) * 0.025F;
        this.zd = zSpeed + (this.random.nextFloat() - 0.5F) * 0.01F;
        this.seedX = this.random.nextDouble() * Mth.TWO_PI;
        this.seedZ = this.random.nextDouble() * Mth.TWO_PI;
        this.seedPhase = this.random.nextDouble() * Mth.TWO_PI;
        this.shade = shade + (this.random.nextFloat() - 0.5F) * 0.08F;
        this.quadSize = 0.25F + this.random.nextFloat() * 0.15F;
        this.initialSize = this.quadSize;
        this.targetSize = this.initialSize * 2.15F * (0.72F + Mth.sqrt(this.random.nextFloat()) * 0.5F);
        this.initialAlpha = 0.85F;
        this.alpha = this.initialAlpha;
        this.applyShape(0.0F);
    }

    @Override
    public void tick() {
        boolean inWater = this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER);
        ((WindBlown) this).spicedcider$setWindResponse(inWater ? 0.0F : AIR_WIND_RESPONSE);
        if (this.age + 1 < this.lifetime) this.curl(inWater);
        super.tick();
        this.applyShape(Mth.clamp((float) this.age / Math.max(this.lifetime, 1), 0.0F, 1.0F));
    }

    private void curl(boolean inWater) {
        double normalizedAge = Mth.clamp((double) this.age / Math.max(this.lifetime, 1), 0.0, 1.0);
        double ageRamp = smooth(Mth.clamp(normalizedAge * 1.35, 0.0, 1.0));
        double heightRamp = smooth(Mth.clamp((this.y - this.sourceY) / 12.0, 0.0, 1.0));
        float turbulence = inWater || !ClientWind.active() ? WATER_TURBULENCE : Math.max(0.25F, ClientWind.turbulence());
        double time = ClientWind.time();
        double strength = (1.2E-4 + turbulence * 0.0011) * (0.25 + Math.max(ageRamp, heightRamp));
        double t = this.age * 0.075 + time * 0.9;
        this.xd += Math.sin(t + this.seedX) * strength + Math.sin(t * 1.9 + this.seedPhase) * strength * 0.45;
        this.zd += Math.cos(t + this.seedZ) * strength + Math.cos(t * 2.1 + this.seedPhase * 1.3) * strength * 0.45;
        this.yd += (1.0 - normalizedAge) * 0.0009 +Math.sin(t * 1.35 + this.seedPhase) * strength * 0.4;
    }

    private void applyShape(float ageFactor) {
        float growth = ageFactor * Mth.sqrt(ageFactor);
        this.quadSize = Mth.lerp(growth, this.initialSize, this.targetSize);
        float fade = ageFactor <= HOLD_UNTIL ? 0.0F : (ageFactor - HOLD_UNTIL) / (1.0F - HOLD_UNTIL);
        this.alpha = this.initialAlpha * Math.max(0.0F, 1.0F - fade * fade);
        float brighten = Mth.clamp(ageFactor / 0.3F, 0.0F, 1.0F);
        float color = Mth.lerp(brighten, this.shade * 0.66F, Math.min(this.shade * 1.08F, 1.0F));
        this.rCol = color;
        this.gCol = color;
        this.bCol = color;
    }

    private static double smooth(double value) {
        return value * value * (3.0 - 2.0 * value);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return MERGED_PLUME;
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
