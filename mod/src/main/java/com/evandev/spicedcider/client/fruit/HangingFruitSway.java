package com.evandev.spicedcider.client.fruit;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.modernfix.HangingFruitLazyModelWrapping;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.wind.client.ClientWind;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public final class HangingFruitSway {
    private static final List<ResourceLocation> FRUIT_BLOCKS = List.of(
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_apple"),
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_orange"),
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_lemon"),
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_plum"),
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_hoary_apple"),
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_walnuts"),
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_golden_apple"),
            ResourceLocation.fromNamespaceAndPath("bountifulfares", "hanging_withered_golden_apple"),
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "hanging_pear"),
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "hanging_avocado"));
    private static final double CLAIM_DISTANCE = 48.0;
    private static final double CLAIM_DISTANCE_SQR = CLAIM_DISTANCE * CLAIM_DISTANCE;
    private static final double REBUILD_NEAR_SQR = 40.0 * 40.0;
    private static final double REBUILD_FAR_SQR = 56.0 * 56.0;
    private static final int SCAN_INTERVAL = 10;
    private static final int PENDING_RESET_INTERVAL = 100;
    private static final float MAX_TILT = 35.0F;
    private static final float WIND_TILT = 24.0F;
    private static final float FLUTTER_TILT = 10.0F;
    private static final float SPRING = 0.05F;
    private static final float DAMPING = 0.92F;

    private static final Set<Long> SWAYING = ConcurrentHashMap.newKeySet();
    private static final Set<Long> STATIONARY = ConcurrentHashMap.newKeySet();
    private static final Set<Long> PENDING = ConcurrentHashMap.newKeySet();
    private static final Map<Long, Swing> SWINGS = new ConcurrentHashMap<>();

    private static volatile Set<Block> blocks;
    private static volatile boolean enabled;
    private static volatile boolean cameraKnown;
    private static volatile double cameraX;
    private static volatile double cameraY;
    private static volatile double cameraZ;
    private static ClientLevel activeLevel;
    private static long lastScan = Long.MIN_VALUE;
    private static long lastPendingReset = Long.MIN_VALUE;

    static boolean claim(BlockPos pos) {
        if (!enabled || !cameraKnown) return false;
        long key = pos.asLong();
        PENDING.remove(key);
        if (distanceSqr(pos) < CLAIM_DISTANCE_SQR) {
            STATIONARY.remove(key);
            SWAYING.add(key);
            return true;
        }
        SWAYING.remove(key);
        SWINGS.remove(key);
        STATIONARY.add(key);
        return false;
    }

    public static Set<ModelResourceLocation> modelLocations() {
        Set<ModelResourceLocation> locations = new HashSet<>();
        for (Block block : blocks()) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                locations.add(BlockModelShaper.stateToModelLocation(state));
            }
        }
        return locations;
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        if (ModList.get().isLoaded("modernfix") && HangingFruitLazyModelWrapping.bakesLazily(event.getModelBakery())) return;
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        for (ModelResourceLocation location : modelLocations()) {
            BakedModel model = models.get(location);
            if (model != null && !(model instanceof SwayingFruitModel)) models.put(location, new SwayingFruitModel(model));
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;
        if (level != activeLevel) {
            activeLevel = level;
            clear();
        }

        Vec3 camera = event.getCamera().getPosition();
        cameraX = camera.x;
        cameraY = camera.y;
        cameraZ = camera.z;
        cameraKnown = true;

        boolean wanted = ClientWind.active() && SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.hangingFruitSway, true);
        if (wanted != enabled) {
            enabled = wanted;
            clear();
            minecraft.levelRenderer.allChanged();
            return;
        }
        if (!enabled) return;

        long gameTime = level.getGameTime();
        if (gameTime - lastPendingReset >= PENDING_RESET_INTERVAL || gameTime < lastPendingReset) {
            lastPendingReset = gameTime;
            PENDING.clear();
        }
        if (gameTime - lastScan >= SCAN_INTERVAL || gameTime < lastScan) {
            lastScan = gameTime;
            scan(level, minecraft.levelRenderer);
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        float time = gameTime + partialTick;
        BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        PoseStack poseStack = event.getPoseStack();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (Long key : SWAYING) {
            pos.set(key);
            BlockState state = level.getBlockState(pos);
            if (!blocks().contains(state.getBlock())) {
                SWAYING.remove(key);
                SWINGS.remove(key);
                continue;
            }
            if (!event.getFrustum().isVisible(new AABB(pos).inflate(0.5))) continue;
            Swing swing = SWINGS.computeIfAbsent(key, k -> new Swing(time));
            swing.update(pos, time);
            draw(level, pos, state, swing, dispatcher, buffers, poseStack, camera);
        }
        buffers.endBatch();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        activeLevel = null;
        enabled = false;
        cameraKnown = false;
        clear();
    }

    private static void scan(ClientLevel level, LevelRenderer renderer) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Long key : STATIONARY) {
            pos.set(key);
            if (!level.isLoaded(pos) || !blocks().contains(level.getBlockState(pos).getBlock())) {
                STATIONARY.remove(key);
                continue;
            }
            if (distanceSqr(pos) < REBUILD_NEAR_SQR && PENDING.add(key)) markDirty(renderer, pos);
        }
        for (Long key : SWAYING) {
            pos.set(key);
            if (!level.isLoaded(pos)) {
                SWAYING.remove(key);
                SWINGS.remove(key);
                continue;
            }
            if (distanceSqr(pos) > REBUILD_FAR_SQR && PENDING.add(key)) markDirty(renderer, pos);
        }
    }

    private static void markDirty(LevelRenderer renderer, BlockPos pos) {
        renderer.setSectionDirty(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    private static void draw(ClientLevel level, BlockPos pos, BlockState state, Swing swing, BlockRenderDispatcher dispatcher,
                             MultiBufferSource buffers, PoseStack poseStack, Vec3 camera) {
        poseStack.pushPose();
        poseStack.translate(pos.getX() - camera.x + 0.5, pos.getY() - camera.y + 1.0, pos.getZ() - camera.z + 0.5);
        float tilt = Mth.sqrt(swing.angleX * swing.angleX + swing.angleZ * swing.angleZ);
        if (tilt > 0.01F) {
            poseStack.mulPose(new Quaternionf().fromAxisAngleDeg(-swing.angleZ / tilt, 0.0F, swing.angleX / tilt, tilt));
        }
        poseStack.translate(-0.5, -1.0, -0.5);
        int light = LevelRenderer.getLightColor(level, pos);
        dispatcher.renderSingleBlock(state, poseStack, buffers, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        poseStack.popPose();
    }

    private static Set<Block> blocks() {
        Set<Block> current = blocks;
        if (current == null) {
            Set<Block> found = new HashSet<>();
            for (ResourceLocation id : FRUIT_BLOCKS) {
                BuiltInRegistries.BLOCK.getOptional(id).ifPresent(found::add);
            }
            current = Set.copyOf(found);
            blocks = current;
        }
        return current;
    }

    private static double distanceSqr(BlockPos pos) {
        double dx = pos.getX() + 0.5 - cameraX;
        double dy = pos.getY() + 0.5 - cameraY;
        double dz = pos.getZ() + 0.5 - cameraZ;
        return dx * dx + dy * dy + dz * dz;
    }

    private static void clear() {
        SWAYING.clear();
        STATIONARY.clear();
        PENDING.clear();
        SWINGS.clear();
        lastScan = Long.MIN_VALUE;
        lastPendingReset = Long.MIN_VALUE;
    }

    private static final class Swing {
        private float angleX;
        private float angleZ;
        private float velocityX;
        private float velocityZ;
        private float lastTime;

        private Swing(float time) {
            lastTime = time;
        }

        private void update(BlockPos pos, float time) {
            float delta = Mth.clamp(time - lastTime, 0.0F, 2.0F);
            lastTime = time;
            if (delta <= 0.0F) return;

            float strength = 0.45F * ClientWind.strengthAt(pos.getY() + 0.5) + 0.55F * ClientWind.exposedStrength(pos);
            float turbulence = ClientWind.turbulence();
            float windX = ClientWind.directionX();
            float windZ = ClientWind.directionZ();
            float phase = phase(pos);
            float windTime = ClientWind.time();
            float gust = 0.8F + 0.2F * Mth.sin(windTime * 0.13F + phase);
            float lean = WIND_TILT * strength * gust;
            float flutter = FLUTTER_TILT * strength * (0.35F + turbulence)
                    * Mth.sin(windTime * 0.37F + phase * 2.0F) * Mth.sin(windTime * 0.11F + phase);
            float targetX = windX * lean - windZ * flutter;
            float targetZ = windZ * lean + windX * flutter;

            velocityX += (targetX - angleX) * SPRING * delta;
            velocityZ += (targetZ - angleZ) * SPRING * delta;
            float damping = (float) Math.pow(DAMPING, delta);
            velocityX *= damping;
            velocityZ *= damping;
            angleX += velocityX * delta;
            angleZ += velocityZ * delta;

            float tilt = Mth.sqrt(angleX * angleX + angleZ * angleZ);
            if (tilt > MAX_TILT) {
                float scale = MAX_TILT / tilt;
                angleX *= scale;
                angleZ *= scale;
                velocityX *= 0.5F;
                velocityZ *= 0.5F;
            }
        }

        private static float phase(BlockPos pos) {
            int hash = Mth.murmurHash3Mixer(pos.getX() * 73428767 ^ pos.getY() * 9122719 ^ pos.getZ() * 42317861);
            return (hash & 65535) / 65535.0F * Mth.TWO_PI;
        }
    }
}
