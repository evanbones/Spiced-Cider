package com.evandev.spicedcider.compat.lodged;

import com.evandev.spicedcider.compat.vista.VistaFeeds;
import com.evandev.spicedcider.networking.PullTamedArrowPayload;
import com.jvn.lodged.client.LodgedArrowRenderHelper;
import com.jvn.lodged.client.LodgedInventoryArrowUi;
import com.jvn.lodged.network.ClientArrowState;
import com.jvn.lodged.world.LodgedArrowVisual;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class TamedArrowPullingClient {
    private static final float ARROW_TIP = 0.225F;
    private static final float ARROW_TAIL = -0.675F;
    private static final int SEGMENT_SAMPLES = 12;
    private static final double PICK_RADIUS = 0.11;
    private static final double REACH_PADDING = 1.0;
    private static final double RECORD_DISTANCE_SQR = 12.0 * 12.0;
    private static final int OUTLINE_BUFFER_SIZE = 1536;

    private static final List<Candidate> CANDIDATES = new ArrayList<>();
    private static boolean recording;
    private static boolean useHeld;
    private static boolean worldTooltip;
    @Nullable
    private static Candidate hovered;

    private record Candidate(LivingEntity entity, int index, LodgedArrowVisual arrow, Matrix4f pose, Matrix3f normal,
                             Vector3f direction, Vector3f tail, Vector3f tip, int light) {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(TamedArrowPullingClient::onRenderFrame);
        NeoForge.EVENT_BUS.addListener(TamedArrowPullingClient::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(TamedArrowPullingClient::onInteraction);
        NeoForge.EVENT_BUS.addListener(TamedArrowPullingClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(TamedArrowPullingClient::onRenderGui);
    }

    public static boolean renderingWorldTooltip() {
        return worldTooltip;
    }

    public static void record(LivingEntity entity, LodgedArrowVisual arrow, PoseStack.Pose pose, @Nullable Vector3f partDirection, int light) {
        if (!recording || VistaFeeds.rendering()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || entity.distanceToSqr(player) > RECORD_DISTANCE_SQR || !TamedArrowPulling.canPull(player, entity)) return;
        List<LodgedArrowVisual> arrows = ClientArrowState.entityArrows(entity).arrows();
        int index = -1;
        for (int i = 0; i < arrows.size(); i++) {
            if (arrows.get(i) == arrow) {
                index = i;
                break;
            }
        }
        if (index < 0) return;
        Vector3f direction = partDirection != null
                ? new Vector3f(partDirection)
                : new Vector3f(arrow.directionX(), arrow.directionY(), arrow.directionZ());
        if (direction.lengthSquared() < 1.0E-6F) return;
        direction.normalize();
        Matrix4f matrix = new Matrix4f(pose.pose());
        Vector3f tail = matrix.transformPosition(new Vector3f(direction).mul(ARROW_TAIL));
        Vector3f tip = matrix.transformPosition(new Vector3f(direction).mul(ARROW_TIP));
        CANDIDATES.add(new Candidate(entity, index, arrow, matrix, new Matrix3f(pose.normal()), direction, tail, tip, light));
    }

    private static void onRenderFrame(RenderFrameEvent.Pre event) {
        CANDIDATES.clear();
        recording = TamedArrowPulling.enabled() && Minecraft.getInstance().level != null;
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || VistaFeeds.rendering()) return;
        recording = false;
        hovered = pick(event.getCamera());
        CANDIDATES.clear();
        if (hovered != null) {
            renderOutline(hovered, event.getPartialTick().getGameTimeDeltaPartialTick(false));
        }
    }

    @Nullable
    private static Candidate pick(Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.screen != null || player.isUsingItem() || CANDIDATES.isEmpty()) return null;
        Vec3 eye = camera.getPosition();
        Vector3f look = camera.getLookVector();
        double reach = player.entityInteractionRange() + REACH_PADDING;
        HitResult hit = minecraft.hitResult;
        double blockDistance = Double.MAX_VALUE;
        Entity hitEntity = null;
        double entityDistance = Double.MAX_VALUE;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            blockDistance = hit.getLocation().distanceTo(eye);
        } else if (hit instanceof EntityHitResult entityHit) {
            hitEntity = entityHit.getEntity();
            entityDistance = hit.getLocation().distanceTo(eye);
        }
        Candidate best = null;
        double bestOffset = PICK_RADIUS;
        Vector3f point = new Vector3f();
        for (Candidate candidate : CANDIDATES) {
            for (int i = 0; i <= SEGMENT_SAMPLES; i++) {
                candidate.tail.lerp(candidate.tip, i / (float) SEGMENT_SAMPLES, point);
                double along = point.dot(look);
                if (along <= 0.0 || along > reach || along > blockDistance) continue;
                if (hitEntity != null && hitEntity != candidate.entity && along > entityDistance) continue;
                if (hitEntity == candidate.entity && along > entityDistance + candidate.entity.getBbWidth() * 0.5) continue;
                double offset = Math.sqrt(Math.max(0.0, point.lengthSquared() - along * along));
                if (offset < bestOffset) {
                    bestOffset = offset;
                    best = candidate;
                }
            }
        }
        return best;
    }

    private static void renderOutline(Candidate candidate, float partialTick) {
        if (!LodgedInventoryArrowUi.prepareArrowOutlineTarget()) return;
        Minecraft minecraft = Minecraft.getInstance();
        try (ByteBufferBuilder bytes = new ByteBufferBuilder(OUTLINE_BUFFER_SIZE)) {
            OutlineBufferSource outline = new OutlineBufferSource(MultiBufferSource.immediate(bytes));
            LodgedInventoryArrowUi.OutlineColor color = LodgedInventoryArrowUi.tamedMobRiskOutlineColor(candidate.arrow);
            outline.setColor(color.red(), color.green(), color.blue(), color.alpha());
            try {
                PoseStack poseStack = new PoseStack();
                poseStack.last().pose().set(candidate.pose);
                poseStack.last().normal().set(candidate.normal);
                LivingEntity entity = candidate.entity;
                AbstractArrow arrow = LodgedArrowRenderHelper.createRenderedArrow(
                        entity.level(), entity.getX(), entity.getY(), entity.getZ(), candidate.arrow, candidate.direction);
                minecraft.getEntityRenderDispatcher().render(arrow, 0.0, 0.0, 0.0, 0.0F, partialTick, poseStack, outline, candidate.light);
                outline.endOutlineBatch();
            } finally {
                LodgedInventoryArrowUi.restoreVanillaEntityTarget();
            }
        }
        minecraft.getMainRenderTarget().bindWrite(false);
        LodgedInventoryArrowUi.processArrowOutlineTarget();
    }

    private static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem() || event.getHand() != InteractionHand.MAIN_HAND) return;
        Candidate target = hovered;
        if (target == null || !target.entity.isAlive()) return;
        event.setCanceled(true);
        if (useHeld) {
            event.setSwingHand(false);
            return;
        }
        useHeld = true;
        event.setSwingHand(true);
        PacketDistributor.sendToServer(new PullTamedArrowPayload(target.entity.getId(), target.index));
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.keyUse.isDown()) useHeld = false;
        if (minecraft.player == null || minecraft.screen != null) hovered = null;
    }

    private static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Candidate target = hovered;
        if (target == null || minecraft.screen != null || minecraft.options.hideGui) return;
        int x = event.getGuiGraphics().guiWidth() / 2;
        int y = event.getGuiGraphics().guiHeight() / 2;
        worldTooltip = true;
        try {
            LodgedInventoryArrowUi.renderHorseBodyRemovalTooltip(event.getGuiGraphics(), target.arrow, x, y);
        } finally {
            worldTooltip = false;
        }
    }
}
