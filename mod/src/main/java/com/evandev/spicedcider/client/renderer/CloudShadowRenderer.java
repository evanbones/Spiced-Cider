package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.compat.betterclouds.BetterCloudsCompat;
import com.evandev.spicedcider.compat.betterclouds.CloudField;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public final class CloudShadowRenderer {

    private static final float SHADOW_TINT_RED = 0.58f;
    private static final float SHADOW_TINT_GREEN = 0.62f;
    private static final float SHADOW_TINT_BLUE = 0.76f;

    @Nullable
    private static ShaderInstance shader;
    @Nullable
    private static RenderTarget depthCopy;
    @Nullable
    private static CloudCoverageTexture coverage;

    private CloudShadowRenderer() {
    }

    public static void setShader(@Nullable ShaderInstance instance) {
        shader = instance;
    }

    public static void render(RenderLevelStageEvent event) {
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.cloudShadows, true)) return;
        if (!BetterCloudsCompat.isLoaded()) return;

        ShaderInstance instance = shader;
        if (instance == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;
        if (minecraft.options.getCloudsType() == CloudStatus.OFF) return;

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        CloudField field = BetterCloudsCompat.field(level, partialTick);
        if (field == null) return;

        float sunAngle = level.getSunAngle(partialTick);
        float sunX = -Mth.sin(sunAngle);
        float sunY = Mth.cos(sunAngle);
        float sunFactor = smoothstep(0.12f, 0.35f, sunY);
        if (sunFactor <= 0) return;

        float rain = level.getRainLevel(partialTick);
        float strength = SpicedCiderConfig.CLIENT.cloudShadowStrength.get().floatValue()
                * sunFactor
                * (1f - 0.85f * rain);
        if (strength <= 1f / 255f) return;

        Vec3 camera = event.getCamera().getPosition();
        int centerGridX = Mth.floor((camera.x - field.originX()) / field.spacing());
        int centerGridZ = Mth.floor((camera.z - field.originZ()) / field.spacing());

        if (coverage == null) coverage = new CloudCoverageTexture();
        int dilateRadius = Mth.clamp(Math.round(field.sizeXZ() * 0.5f / field.spacing()), 0, 4);
        float softness = SpicedCiderConfig.CLIENT.cloudShadowSoftness.get();
        int blurRadius = Mth.clamp(Math.round(softness / field.spacing()), 0, 4);
        coverage.update(field, centerGridX, centerGridZ, dilateRadius, blurRadius);

        RenderTarget main = minecraft.getMainRenderTarget();
        if (depthCopy == null || depthCopy.width != main.width || depthCopy.height != main.height) {
            if (depthCopy != null) depthCopy.destroyBuffers();
            depthCopy = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
        }
        depthCopy.copyDepthFrom(main);
        main.bindWrite(false);

        Matrix4f inverseViewProjection = new Matrix4f(event.getProjectionMatrix())
                .mul(event.getModelViewMatrix())
                .invert();

        float extent = field.spacing() * CloudCoverageTexture.SIZE;
        float coverageWorldX = (float) (coverage.originGridX() * field.spacing() + field.originX() - field.spacing() * 0.5);
        float coverageWorldZ = (float) (coverage.originGridZ() * field.spacing() + field.originZ() - field.spacing() * 0.5);
        float fadeEnd = field.renderDistance();
        if (fadeEnd <= 0) return;

        instance.setSampler("DepthSampler", depthCopy.getDepthTextureId());
        instance.setSampler("CoverageSampler", coverage.textureId());
        instance.safeGetUniform("InvViewProjMat").set(inverseViewProjection);
        instance.safeGetUniform("CameraPos").set((float) camera.x, (float) camera.y, (float) camera.z);
        instance.safeGetUniform("SunDir").set(sunX, sunY, 0f);
        instance.safeGetUniform("CloudPlane").set(field.cloudHeight(), Math.max(field.spacing() * 0.5f, softness * 0.5f));
        instance.safeGetUniform("CoverageOrigin").set(coverageWorldX, coverageWorldZ, 1f / extent, 0f);
        instance.safeGetUniform("ShadowColor").set(SHADOW_TINT_RED, SHADOW_TINT_GREEN, SHADOW_TINT_BLUE, strength);
        instance.safeGetUniform("FadeParams").set(fadeEnd * 0.6f, fadeEnd);

        RenderSystem.setShader(() -> instance);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.DST_COLOR,
                GlStateManager.DestFactor.ZERO,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        builder.addVertex(-1f, -1f, 0f);
        builder.addVertex(1f, -1f, 0f);
        builder.addVertex(1f, 1f, 0f);
        builder.addVertex(-1f, 1f, 0f);
        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void onLevelChanged() {
        BetterCloudsCompat.invalidate();
        if (coverage != null) {
            coverage.close();
            coverage = null;
        }
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = Mth.clamp((value - edge0) / (edge1 - edge0), 0f, 1f);
        return t * t * (3f - 2f * t);
    }
}
