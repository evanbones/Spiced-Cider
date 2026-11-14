package com.evandev.spicedcider.wind.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public final class WindMotes {
    public static final TagKey<Block> SOURCES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "wind_mote_sources"));
    private static final int SAMPLES_PER_TICK = 8;
    private static final int RADIUS = 18;
    private static final float MIN_STRENGTH = 0.12F;

    static void tick(Minecraft minecraft, ClientLevel level, Vec3 camera) {
        if (ClientWind.strength() < MIN_STRENGTH) return;
        RandomSource random = level.random;
        boolean enclosed = level.dimensionType().hasCeiling();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < SAMPLES_PER_TICK; i++) {
            int x = Mth.floor(camera.x) + random.nextInt(RADIUS * 2 + 1) - RADIUS;
            int z = Mth.floor(camera.z) + random.nextInt(RADIUS * 2 + 1) - RADIUS;
            int top = enclosed ? floorBelow(level, pos, x, Mth.floor(camera.y) + 4, z) : level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            if (top == Integer.MIN_VALUE || Math.abs(top - camera.y) > RADIUS) continue;
            pos.set(x, top - 1, z);
            BlockState state = level.getBlockState(pos);
            if (!state.is(SOURCES)) continue;
            if (level.isRaining() && level.isRainingAt(pos.above())) continue;
            double spawnY = top + 0.1 + random.nextDouble() * 0.5;
            float strength = ClientWind.exposedStrength(x + 0.5, spawnY, z + 0.5);
            if (random.nextFloat() >= Mth.clamp((strength - MIN_STRENGTH) * 0.5F, 0.0F, 0.5F)) continue;
            int color = minecraft.getBlockColors().getColor(state, level, pos, 0);
            if (color == -1) color = state.getMapColor(level, pos).col;
            float shade = 0.85F + random.nextFloat() * 0.3F;
            level.addParticle(ModParticleTypes.WIND_MOTE.get(), x + random.nextDouble(), spawnY, z + random.nextDouble(),
                    Math.min(1.0F, (color >> 16 & 0xFF) / 255.0F * shade),
                    Math.min(1.0F, (color >> 8 & 0xFF) / 255.0F * shade),
                    Math.min(1.0F, (color & 0xFF) / 255.0F * shade));
        }
    }

    private static int floorBelow(ClientLevel level, BlockPos.MutableBlockPos pos, int x, int startY, int z) {
        boolean wasAir = false;
        for (int y = startY; y > startY - 12; y--) {
            boolean air = level.getBlockState(pos.set(x, y, z)).isAir();
            if (!air && wasAir) return y + 1;
            wasAir = air;
        }
        return Integer.MIN_VALUE;
    }
}
