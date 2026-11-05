package com.evandev.spicedcider.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CreakingEyesParticle extends TextureSheetParticle {
    public static final int MAX_LIGHT = 7;
    private static final int BLINK_TICKS = 6;

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
            this.lifetime = Math.min(this.lifetime, this.age + BLINK_TICKS);
        }
    }

    private float openness(float partialTicks) {
        return Mth.clamp((this.lifetime - this.age - partialTicks) / BLINK_TICKS, 0.0F, 1.0F);
    }

    @Override
    protected void renderRotatedQuad(@NotNull VertexConsumer buffer, @NotNull Quaternionf quaternion, float x, float y, float z, float partialTicks) {
        float open = this.openness(partialTicks);
        if (open <= 0.0F) return;
        float size = this.getQuadSize(partialTicks);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float vMid = (this.getV0() + this.getV1()) * 0.5F;
        float vHalf = (this.getV1() - this.getV0()) * 0.5F * open;
        float v0 = vMid - vHalf;
        float v1 = vMid + vHalf;
        int light = this.getLightColor(partialTicks);
        this.vertex(buffer, quaternion, x, y, z, 1.0F, -open, size, u1, v1, light);
        this.vertex(buffer, quaternion, x, y, z, 1.0F, open, size, u1, v0, light);
        this.vertex(buffer, quaternion, x, y, z, -1.0F, open, size, u0, v0, light);
        this.vertex(buffer, quaternion, x, y, z, -1.0F, -open, size, u0, v1, light);
    }

    private void vertex(VertexConsumer buffer, Quaternionf quaternion, float x, float y, float z, float xOffset, float yOffset,
                        float size, float u, float v, int light) {
        Vector3f pos = new Vector3f(xOffset, yOffset, 0.0F).rotate(quaternion).mul(size).add(x, y, z);
        buffer.addVertex(pos.x(), pos.y(), pos.z()).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
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
