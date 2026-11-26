package com.evandev.spicedcider.compat.beams.client;

import com.syl.beams.blockentities.BeamBlockEntity;
import com.syl.beams.client.renderer.beam.BeamTextures;
import com.syl.beams.targeting.BeamTargeting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class BeamParticles {
    private static final int BREAK_PARTICLES_PER_BLOCK = 6;
    private static final int MAX_BREAK_PARTICLES = 96;

    public static boolean replaceHitEffects(ClientLevel level, ParticleEngine engine) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        Vec3 origin = player.getEyePosition(1.0F);
        Vec3 end = origin.add(player.getViewVector(1.0F).scale(player.blockInteractionRange()));
        Optional<BeamTargeting.Result> found = BeamTargeting.findClient(level, origin, end);
        if (found.isEmpty()) return false;
        BeamTargeting.Result result = found.get();
        TextureAtlasSprite sprite = BeamTextures.plankSprite(result.link().woodType);
        if (sprite == null) return true;
        RandomSource random = level.random;
        Vec3 hit = result.hitPoint();
        engine.add(new BeamParticle(level,
                hit.x + (random.nextDouble() - 0.5) * 0.2,
                hit.y + (random.nextDouble() - 0.5) * 0.2,
                hit.z + (random.nextDouble() - 0.5) * 0.2,
                0.0, 0.0, 0.0, sprite).setPower(0.2F).scale(0.6F));
        return true;
    }

    public static void breakBurst(BeamBlockEntity.BeamLink link) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || link == null || link.otherVec == null) return;
        TextureAtlasSprite sprite = BeamTextures.plankSprite(link.woodType);
        if (sprite == null) return;
        RandomSource random = level.random;
        Vec3 start = link.selfVec;
        Vec3 end = link.otherVec;
        int count = Mth.clamp(Mth.ceil(start.distanceTo(end) * BREAK_PARTICLES_PER_BLOCK), 4, MAX_BREAK_PARTICLES);
        for (int i = 0; i < count; i++) {
            Vec3 at = start.lerp(end, random.nextDouble());
            double vx = (random.nextDouble() - 0.5) * 0.2;
            double vy = random.nextDouble() * 0.15;
            double vz = (random.nextDouble() - 0.5) * 0.2;
            Minecraft.getInstance().particleEngine.add(new BeamParticle(level,
                    at.x + (random.nextDouble() - 0.5) * 0.2,
                    at.y + (random.nextDouble() - 0.5) * 0.2,
                    at.z + (random.nextDouble() - 0.5) * 0.2,
                    vx, vy, vz, sprite));
        }
    }

    private static class BeamParticle extends TerrainParticle {
        BeamParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
            super(level, x, y, z, xSpeed, ySpeed, zSpeed, Blocks.STONE.defaultBlockState());
            setSprite(sprite);
        }
    }
}
