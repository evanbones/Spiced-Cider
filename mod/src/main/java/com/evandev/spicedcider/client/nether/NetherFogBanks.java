package com.evandev.spicedcider.client.nether;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class NetherFogBanks {
    private static final ResourceLocation SHADER = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "nether_fog_bank");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/environment/nether_fog_bank.png");

    private static final int SPAWN_INTERVAL = 6;
    private static final double MIN_DISTANCE = 32.0;
    private static final double MAX_DISTANCE = 80.0;
    private static final double HEIGHT_SPREAD = 8.0;
    private static final float MIN_SIZE = 27.0F;
    private static final float SIZE_SPREAD = 3.0F;
    private static final float GROWTH = 2.5F;
    private static final double DRIFT = 0.05;
    private static final int MIN_LIFETIME = 120;
    private static final int LIFETIME_SPREAD = 60;
    private static final float FADE_IN = 0.17F;
    private static final float FADE_OUT = 0.17F;
    private static final double DISSIPATE_DISTANCE = 28.0;
    private static final int DISSIPATE_SPEED = 10;
    private static final float BRIGHTEN = 2.0F;
    private static final float SOFT_EDGE = 6.0F;

    private static final List<Bank> BANKS = new ArrayList<>();
    private static ShaderInstance shader;
    private static TextureTarget depthCopy;
    private static ClientLevel lastLevel;

    private static final class Bank {
        double x;
        double y;
        double z;
        double xo;
        double yo;
        double zo;
        final double dx;
        final double dy;
        final double dz;
        final float startSize;
        final float endSize;
        final float red;
        final float green;
        final float blue;
        final int lifetime;
        int age;

        Bank(double x, double y, double z, RandomSource random, int fog) {
            this.x = this.xo = x;
            this.y = this.yo = y;
            this.z = this.zo = z;
            this.dx = random.nextDouble() * DRIFT;
            this.dy = random.nextDouble() * DRIFT;
            this.dz = random.nextDouble() * DRIFT;
            this.startSize = MIN_SIZE + random.nextFloat() * SIZE_SPREAD;
            this.endSize = this.startSize + GROWTH + random.nextFloat() * GROWTH;
            this.red = Math.min(1.0F, (fog >> 16 & 255) / 255.0F * BRIGHTEN);
            this.green = Math.min(1.0F, (fog >> 8 & 255) / 255.0F * BRIGHTEN);
            this.blue = Math.min(1.0F, (fog & 255) / 255.0F * BRIGHTEN);
            this.lifetime = MIN_LIFETIME + random.nextInt(LIFETIME_SPREAD);
        }

        boolean tick(Vec3 camera) {
            xo = x;
            yo = y;
            zo = z;
            x += dx;
            y += dy;
            z += dz;
            double horizontal = Math.sqrt((x - camera.x) * (x - camera.x) + (z - camera.z) * (z - camera.z));
            if (horizontal < DISSIPATE_DISTANCE) age += (int) ((1.0 - horizontal / DISSIPATE_DISTANCE) * DISSIPATE_SPEED);
            return ++age < lifetime;
        }

        float opacity(float partialTick) {
            float progress = Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F);
            return Math.min(Mth.clamp(progress / FADE_IN, 0.0F, 1.0F), Mth.clamp((1.0F - progress) / FADE_OUT, 0.0F, 1.0F));
        }

        float size(float partialTick) {
            return Mth.lerp(Mth.clamp((age + partialTick) / lifetime, 0.0F, 1.0F), startSize, endSize);
        }
    }

    public static void registerShader(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), SHADER, DefaultVertexFormat.POSITION_TEX_COLOR), loaded -> shader = loaded);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != lastLevel) {
            BANKS.clear();
            lastLevel = level;
        }
        if (level == null || minecraft.isPaused()) return;

        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
        BANKS.removeIf(bank -> !bank.tick(camera));
        if (!enabled(level)) return;

        ParticleStatus particles = minecraft.options.particles().get();
        if (particles == ParticleStatus.MINIMAL) return;
        int interval = particles == ParticleStatus.DECREASED ? SPAWN_INTERVAL * 2 : SPAWN_INTERVAL;
        if (level.getGameTime() % interval != 0) return;

        RandomSource random = level.random;
        double angle = random.nextDouble() * Math.PI * 2.0;
        double distance = Mth.lerp(random.nextDouble(), MIN_DISTANCE, MAX_DISTANCE);
        double x = camera.x + Math.cos(angle) * distance;
        double y = camera.y + (random.nextDouble() * 2.0 - 1.0) * HEIGHT_SPREAD;
        double z = camera.z + Math.sin(angle) * distance;
        int fog = level.getBiome(BlockPos.containing(x, y, z)).value().getFogColor();
        BANKS.add(new Bank(x, y, z, random, fog));
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || BANKS.isEmpty() || shader == null) return;
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        copyDepth(main);

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 camera = event.getCamera().getPosition();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for (Bank bank : BANKS) {
            float opacity = bank.opacity(partialTick);
            if (opacity <= 0.0F) continue;
            float cx = (float) (Mth.lerp(partialTick, bank.xo, bank.x) - camera.x);
            float cy = (float) (Mth.lerp(partialTick, bank.yo, bank.y) - camera.y);
            float cz = (float) (Mth.lerp(partialTick, bank.zo, bank.z) - camera.z);
            float horizontal = Mth.sqrt(cx * cx + cz * cz);
            if (horizontal < 0.001F) continue;
            float size = bank.size(partialTick);
            float rx = -cz / horizontal * size;
            float rz = cx / horizontal * size;
            buffer.addVertex(cx - rx, cy - size, cz - rz).setUv(0.0F, 1.0F).setColor(bank.red, bank.green, bank.blue, opacity);
            buffer.addVertex(cx + rx, cy - size, cz + rz).setUv(1.0F, 1.0F).setColor(bank.red, bank.green, bank.blue, opacity);
            buffer.addVertex(cx + rx, cy + size, cz + rz).setUv(1.0F, 0.0F).setColor(bank.red, bank.green, bank.blue, opacity);
            buffer.addVertex(cx - rx, cy + size, cz - rz).setUv(0.0F, 0.0F).setColor(bank.red, bank.green, bank.blue, opacity);
        }
        MeshData mesh = buffer.build();
        if (mesh == null) return;

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(() -> shader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        shader.setSampler("DepthSampler", depthCopy.getDepthTextureId());
        shader.safeGetUniform("FadeDistance").set(SOFT_EDGE);
        BufferUploader.drawWithShader(mesh);
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private static boolean enabled(ClientLevel level) {
        return level.dimension() == Level.NETHER
                && SpicedCiderConfig.CLIENT_SPEC.isLoaded()
                && SpicedCiderConfig.CLIENT.netherMist.get();
    }

    private static void copyDepth(RenderTarget main) {
        if (depthCopy == null) {
            depthCopy = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
        } else if (depthCopy.width != main.width || depthCopy.height != main.height) {
            depthCopy.resize(main.width, main.height, Minecraft.ON_OSX);
        }
        depthCopy.copyDepthFrom(main);
        main.bindWrite(false);
    }
}
