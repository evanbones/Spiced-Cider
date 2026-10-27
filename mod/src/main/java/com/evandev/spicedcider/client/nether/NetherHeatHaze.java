package com.evandev.spicedcider.client.nether;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class NetherHeatHaze {
    public static final String EFFECT = "spicedcider:heat_haze";
    private static final ResourceLocation CHAIN = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "shaders/post/heat_haze.json");

    private static final float BASE_STRENGTH = 0.003F;
    private static final float BASE_NEAR = 12.0F;
    private static final float BASE_FAR = 128.0F;
    private static final float LAVA_STRENGTH = 0.004F;
    private static final float LAVA_NEAR = 0.0F;
    private static final float LAVA_FAR = 64.0F;
    private static final int LAVA_RADIUS = 3;
    private static final int LAVA_SOURCES = 3;
    private static final int LAVA_SCAN_TICKS = 20;
    private static final float BLEND = 0.04F;

    private static final Matrix4f INVERSE_PROJECTION = new Matrix4f();

    private static PostChain chain;
    private static boolean failed;
    private static boolean nearLava;
    private static float strength;
    private static float near = BASE_NEAR;
    private static float far = BASE_FAR;
    private static float previousStrength;
    private static float previousNear = BASE_NEAR;
    private static float previousFar = BASE_FAR;
    private static float time;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        previousStrength = strength;
        previousNear = near;
        previousFar = far;
        if (level == null || minecraft.player == null || minecraft.isPaused()) {
            if (level == null) strength = 0.0F;
            return;
        }

        float targetStrength = 0.0F;
        float targetNear = BASE_NEAR;
        float targetFar = BASE_FAR;
        if (active(level)) {
            if (level.getGameTime() % LAVA_SCAN_TICKS == 0) nearLava = scanLava(level, minecraft.player.blockPosition());
            targetStrength = nearLava ? LAVA_STRENGTH : BASE_STRENGTH;
            if (nearLava) {
                targetNear = LAVA_NEAR;
                targetFar = LAVA_FAR;
            }
            targetStrength *= minecraft.options.screenEffectScale().get().floatValue();
        } else {
            nearLava = false;
        }

        strength += (targetStrength - strength) * BLEND;
        near += (targetNear - near) * BLEND;
        far += (targetFar - far) * BLEND;
        if (targetStrength == 0.0F && strength < 0.00002F) strength = 0.0F;
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        Minecraft minecraft = Minecraft.getInstance();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        if (Mth.lerp(partialTick, previousStrength, strength) <= 0.0F || failed) return;
        if (minecraft.gameRenderer.getMainCamera().getFluidInCamera() != FogType.NONE) return;

        event.getProjectionMatrix().invert(INVERSE_PROJECTION);
        time = (event.getRenderTick() + partialTick) / 20.0F;

        RenderTarget main = minecraft.getMainRenderTarget();
        PostChain postChain = chain(minecraft, main);
        if (postChain == null) return;
        postChain.process(partialTick);
        main.bindWrite(false);
    }

    public static void applyUniforms(EffectInstance effect) {
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        effect.safeGetUniform("InvProjMat").set(INVERSE_PROJECTION);
        effect.safeGetUniform("Time").set(time);
        effect.safeGetUniform("Strength").set(Mth.lerp(partialTick, previousStrength, strength));
        effect.safeGetUniform("Range").set(Mth.lerp(partialTick, previousNear, near), Mth.lerp(partialTick, previousFar, far));
    }

    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) manager -> close());
    }

    private static boolean active(ClientLevel level) {
        return level.dimension() == Level.NETHER
                && SpicedCiderConfig.CLIENT_SPEC.isLoaded()
                && SpicedCiderConfig.CLIENT.netherHeatHaze.get();
    }

    private static boolean scanLava(ClientLevel level, BlockPos center) {
        int found = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -LAVA_RADIUS; dx <= LAVA_RADIUS; dx++) {
            for (int dy = -LAVA_RADIUS; dy <= LAVA_RADIUS; dy++) {
                for (int dz = -LAVA_RADIUS; dz <= LAVA_RADIUS; dz++) {
                    pos.setWithOffset(center, dx, dy, dz);
                    var fluid = level.getFluidState(pos);
                    if (fluid.is(FluidTags.LAVA) && fluid.isSource() && ++found >= LAVA_SOURCES) return true;
                }
            }
        }
        return false;
    }

    private static PostChain chain(Minecraft minecraft, RenderTarget main) {
        if (chain == null) {
            try {
                chain = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), main, CHAIN);
                chain.resize(main.width, main.height);
            } catch (Exception exception) {
                SpicedCider.LOGGER.error("Failed to load the Nether heat haze shader", exception);
                failed = true;
                chain = null;
                return null;
            }
        } else if (chain.getTempTarget("swap").width != main.width || chain.getTempTarget("swap").height != main.height) {
            chain.resize(main.width, main.height);
        }
        return chain;
    }

    private static void close() {
        if (chain != null) chain.close();
        chain = null;
        failed = false;
    }
}
