package com.evandev.spicedcider.compat.picturemode;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.voxy.BundledRoxy;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class PictureModeClouds {
    private static final boolean LOADED = ModList.get().isLoaded("picturemode");
    private static final boolean SODIUM = ModList.get().isLoaded("sodium");
    private static final int SEGMENTS = 256;
    private static final float SINK = 3.0F;

    private static float ringRadius;

    public static boolean active() {
        return LOADED && SpicedCiderConfig.CLIENT_SPEC.isLoaded() && SpicedCiderConfig.CLIENT.pictureModeClouds.get() && PictureModeBridge.enabled();
    }

    private static int ground(ClientLevel level, int x, int z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1, z);
        while (pos.getY() > level.getMinBuildHeight()) {
            BlockState state = level.getBlockState(pos);
            if (state.isSolidRender(level, pos) && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS)) break;
            pos.move(0, -1, 0);
        }
        return pos.getY() + 1;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getMode() != FogRenderer.FogMode.FOG_TERRAIN || event.getCamera().getFluidInCamera() != FogType.NONE || !active()) return;
        float end = Math.max(Minecraft.getInstance().options.getEffectiveRenderDistance() - 1, 2) * 16.0F;
        float start = end - Mth.clamp(end / 10.0F, 4.0F, 64.0F);
        event.setFarPlaneDistance(end);
        event.setNearPlaneDistance(start);
        event.setFogShape(FogShape.CYLINDER);
        event.setCanceled(true);
        BundledRoxy.overrideTerrainFog(start, end, FogShape.CYLINDER);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (!active()) return;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            ringRadius = RenderSystem.getShaderFogEnd();
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || ringRadius <= 0.0F || ringRadius > 4096.0F) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        Vec3 camera = event.getCamera().getPosition();
        float[] color = RenderSystem.getShaderFogColor();
        float bottom = (float) (level.getMinBuildHeight() - camera.y);
        float[] xs = new float[SEGMENTS + 1];
        float[] zs = new float[SEGMENTS + 1];
        float[] tops = new float[SEGMENTS + 1];
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = i * Mth.TWO_PI / SEGMENTS;
            float cos = Mth.cos(angle), sin = Mth.sin(angle);
            float radius = ringRadius;
            int surface = level.getMinBuildHeight();
            while (radius > 16.0F) {
                int x = Mth.floor(camera.x + cos * radius), z = Mth.floor(camera.z + sin * radius);
                if (level.hasChunk(x >> 4, z >> 4)) {
                    surface = ground(level, x, z);
                    if (!SODIUM || SodiumSections.built(x >> 4, surface - 1, z >> 4)) break;
                }
                radius -= 4.0F;
            }
            xs[i] = cos * radius;
            zs[i] = sin * radius;
            tops[i] = (float) (surface - SINK - camera.y);
        }

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < SEGMENTS; i++) {
            float top = Math.min(tops[i], tops[i + 1]);
            if (top <= bottom) continue;
            buffer.addVertex(xs[i], bottom, zs[i]).setColor(color[0], color[1], color[2], 1.0F);
            buffer.addVertex(xs[i], top, zs[i]).setColor(color[0], color[1], color[2], 1.0F);
            buffer.addVertex(xs[i + 1], top, zs[i + 1]).setColor(color[0], color[1], color[2], 1.0F);
            buffer.addVertex(xs[i + 1], bottom, zs[i + 1]).setColor(color[0], color[1], color[2], 1.0F);
        }
        MeshData mesh = buffer.build();
        if (mesh == null) return;

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(mesh);
        RenderSystem.enableCull();
    }
}
