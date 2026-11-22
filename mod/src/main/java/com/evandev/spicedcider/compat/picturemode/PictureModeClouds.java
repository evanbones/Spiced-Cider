package com.evandev.spicedcider.compat.picturemode;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.voxy.BundledRoxy;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.pipeline.TextureTarget;
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
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class PictureModeClouds {
    private static final boolean LOADED = ModList.get().isLoaded("picturemode");
    private static final boolean SODIUM = ModList.get().isLoaded("sodium");
    private static final int SEGMENTS = 256;
    private static final float SINK = 3.0F;

    private static final ResourceLocation DEPTH_FOG = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "picture_mode_depth_fog");

    private static float ringRadius;
    private static float fogStart;
    private static ShaderInstance depthFogShader;
    private static TextureTarget depthCopy;
    private static DynamicTexture ringTexture;

    public static void registerShader(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), DEPTH_FOG, DefaultVertexFormat.POSITION), loaded -> depthFogShader = loaded);
    }

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
            fogStart = RenderSystem.getShaderFogStart();
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

        if (Minecraft.getInstance().gameRenderer.currentEffect() != null && depthFogShader != null) {
            uploadRing(xs, zs);
            cutFoggedDepth(event, (fogStart + ringRadius) * 0.5F);
        }
    }

    private static void uploadRing(float[] xs, float[] zs) {
        if (ringTexture == null) {
            ringTexture = new DynamicTexture(SEGMENTS, 1, false);
        }
        NativeImage pixels = ringTexture.getPixels();
        if (pixels == null) return;
        for (int i = 0; i < SEGMENTS; i++) {
            int encoded = Mth.clamp(Math.round(Mth.sqrt(xs[i] * xs[i] + zs[i] * zs[i]) * 16.0F), 0, 0xFFFF);
            pixels.setPixelRGBA(i, 0, 0xFF000000 | (encoded & 0xFF) << 8 | (encoded >> 8) & 0xFF);
        }
        ringTexture.upload();
    }

    private static void cutFoggedDepth(RenderLevelStageEvent event, float distance) {
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (depthCopy == null) {
            depthCopy = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
        } else if (depthCopy.width != main.width || depthCopy.height != main.height) {
            depthCopy.resize(main.width, main.height, Minecraft.ON_OSX);
        }
        depthCopy.copyDepthFrom(main);
        main.bindWrite(false);

        Matrix4f inverse = new Matrix4f(event.getProjectionMatrix()).mul(event.getModelViewMatrix()).invert();
        BufferBuilder quad = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        quad.addVertex(0.0F, 0.0F, 0.0F);
        quad.addVertex(1.0F, 0.0F, 0.0F);
        quad.addVertex(1.0F, 1.0F, 0.0F);
        quad.addVertex(0.0F, 1.0F, 0.0F);

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.depthMask(true);
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.disableBlend();
        RenderSystem.disableCull();
        RenderSystem.setShader(() -> depthFogShader);
        depthFogShader.setSampler("DepthSampler", depthCopy.getDepthTextureId());
        depthFogShader.setSampler("RingSampler", ringTexture.getId());
        depthFogShader.safeGetUniform("InvViewProjMat").set(inverse);
        depthFogShader.safeGetUniform("CutDistance").set(distance);
        BufferUploader.drawWithShader(quad.buildOrThrow());
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableCull();
    }
}
