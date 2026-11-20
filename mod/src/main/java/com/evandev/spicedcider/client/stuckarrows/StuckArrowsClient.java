package com.evandev.spicedcider.client.stuckarrows;

import com.evandev.spicedcider.compat.vista.VistaFeeds;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.stuckarrows.StuckArrowSlots;
import com.evandev.spicedcider.content.stuckarrows.StuckArrows;
import com.evandev.spicedcider.networking.PullStuckArrowPayload;
import com.evandev.spicedcider.networking.StuckArrowsPayload;
import com.evandev.spicedcider.registry.ModAttachments;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class StuckArrowsClient {
    private static final float ARROW_TIP = 0.25F;
    private static final float ARROW_TAIL = -0.65F;
    private static final int SEGMENT_SAMPLES = 12;
    private static final double PICK_RADIUS = 0.11;
    private static final double GUI_PICK_RADIUS = 2.5;
    private static final double REACH_PADDING = 1.0;
    private static final double RECORD_DISTANCE_SQR = 12.0 * 12.0;
    private static final int MODEL_LEFT = 26;
    private static final int MODEL_TOP = 8;
    private static final int MODEL_RIGHT = 75;
    private static final int MODEL_BOTTOM = 78;
    private static final float DRAG_DEGREES_PER_PIXEL = 2.5F;
    private static final int HIGHLIGHT_BUFFER_SIZE = 1536;

    @FunctionalInterface
    public interface Redraw {
        void draw(PoseStack poseStack, MultiBufferSource buffer, int packedLight);
    }

    private record Candidate(LivingEntity entity, int slot, int part, Matrix4f pose, Matrix3f normal,
                             Vector3f tail, Vector3f tip, int light, Redraw redraw) {
    }

    private static final List<Candidate> WORLD = new ArrayList<>();
    private static final List<Candidate> INVENTORY = new ArrayList<>();
    private static boolean recordingWorld;
    @Nullable
    private static LivingEntity inventoryEntity;
    @Nullable
    private static Candidate hoveredWorld;
    @Nullable
    private static Candidate hoveredInventory;
    private static boolean useHeld;
    private static boolean dragging;
    private static float modelYaw;

    public static void register() {
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onRenderFrame);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onInteraction);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onMousePressed);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onMouseDragged);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onMouseReleased);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onScreenRender);
        NeoForge.EVENT_BUS.addListener(StuckArrowsClient::onScreenOpening);
    }

    public static void receive(StuckArrowsPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (!(minecraft.level.getEntity(payload.entityId()) instanceof LivingEntity living)) return;
        if (payload.slots().isEmpty()) {
            living.removeData(ModAttachments.STUCK_ARROWS);
        } else {
            living.setData(ModAttachments.STUCK_ARROWS, new StuckArrowSlots(payload.slots()));
        }
    }

    public static void record(LivingEntity entity, int slot, int part, PoseStack.Pose pose, float x, float y, float z, int light, Redraw redraw) {
        if (!entity.hasData(ModAttachments.STUCK_ARROWS)) return;
        boolean inventory = inventoryEntity == entity;
        if (!inventory) {
            if (!recordingWorld || VistaFeeds.rendering()) return;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || entity == player || entity.distanceToSqr(player) > RECORD_DISTANCE_SQR || !StuckArrows.canPull(player, entity)) return;
        }
        Vector3f direction = new Vector3f(x, y, z);
        if (direction.lengthSquared() < 1.0E-6F) return;
        direction.normalize();
        Matrix4f matrix = new Matrix4f(pose.pose());
        Vector3f tail = matrix.transformPosition(new Vector3f(direction).mul(ARROW_TAIL));
        Vector3f tip = matrix.transformPosition(new Vector3f(direction).mul(ARROW_TIP));
        Candidate candidate = new Candidate(entity, slot, part, matrix, new Matrix3f(pose.normal()), tail, tip, light, redraw);
        (inventory ? INVENTORY : WORLD).add(candidate);
    }

    public static boolean isInventoryModel(LivingEntity entity) {
        return inventoryEntity == entity;
    }

    public static boolean beginInventoryModel(LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        if (entity != minecraft.player || !(minecraft.screen instanceof InventoryScreen)) return false;
        inventoryEntity = entity;
        INVENTORY.clear();
        return true;
    }

    public static float modelYaw() {
        return rotationEnabled() ? modelYaw : 0.0F;
    }

    public static void endInventoryModel(GuiGraphics guiGraphics) {
        inventoryEntity = null;
        Minecraft minecraft = Minecraft.getInstance();
        double scale = (double) minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
        double mouseX = minecraft.mouseHandler.xpos() * scale;
        double mouseY = minecraft.mouseHandler.ypos() * scale;
        hoveredInventory = dragging ? null : pickInventory(mouseX, mouseY);
        INVENTORY.clear();
        Candidate hovered = hoveredInventory;
        if (hovered == null) return;
        guiGraphics.flush();
        Lighting.setupForEntityInInventory();
        MultiBufferSource.BufferSource source = guiGraphics.bufferSource();
        hovered.redraw.draw(pose(hovered), HighlightVertexConsumer.wrap(source), LightTexture.FULL_BRIGHT);
        source.endBatch();
        Lighting.setupFor3DItems();
    }

    private static boolean rotationEnabled() {
        return SpicedCiderConfig.CLIENT_SPEC.isLoaded() && SpicedCiderConfig.CLIENT.inventoryModelRotation.get();
    }

    private static PoseStack pose(Candidate candidate) {
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(candidate.pose);
        poseStack.last().normal().set(candidate.normal);
        return poseStack;
    }

    @Nullable
    private static Candidate pickInventory(double mouseX, double mouseY) {
        Candidate best = null;
        double bestDepth = -Double.MAX_VALUE;
        Vector3f point = new Vector3f();
        for (Candidate candidate : INVENTORY) {
            double closest = Double.MAX_VALUE;
            for (int i = 0; i <= SEGMENT_SAMPLES; i++) {
                candidate.tail.lerp(candidate.tip, i / (float) SEGMENT_SAMPLES, point);
                double dx = point.x - mouseX;
                double dy = point.y - mouseY;
                closest = Math.min(closest, dx * dx + dy * dy);
            }
            if (closest > GUI_PICK_RADIUS * GUI_PICK_RADIUS) continue;
            double depth = (candidate.tail.z + candidate.tip.z) * 0.5;
            if (depth > bestDepth) {
                bestDepth = depth;
                best = candidate;
            }
        }
        return best;
    }

    private static void onRenderFrame(RenderFrameEvent.Pre event) {
        WORLD.clear();
        recordingWorld = Minecraft.getInstance().level != null;
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || VistaFeeds.rendering()) return;
        recordingWorld = false;
        hoveredWorld = pickWorld(event.getCamera());
        WORLD.clear();
        Candidate hovered = hoveredWorld;
        if (hovered == null) return;
        try (ByteBufferBuilder bytes = new ByteBufferBuilder(HIGHLIGHT_BUFFER_SIZE)) {
            MultiBufferSource.BufferSource source = MultiBufferSource.immediate(bytes);
            hovered.redraw.draw(pose(hovered), HighlightVertexConsumer.wrap(source), LightTexture.FULL_BRIGHT);
            source.endBatch();
        }
    }

    @Nullable
    private static Candidate pickWorld(Camera camera) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.screen != null || player.isUsingItem() || WORLD.isEmpty()) return null;
        Vector3f look = camera.getLookVector();
        Vec3 eye = camera.getPosition();
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
        for (Candidate candidate : WORLD) {
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

    private static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem() || event.getHand() != InteractionHand.MAIN_HAND) return;
        Candidate target = hoveredWorld;
        if (target == null || !target.entity.isAlive()) return;
        event.setCanceled(true);
        if (useHeld) {
            event.setSwingHand(false);
            return;
        }
        useHeld = true;
        event.setSwingHand(true);
        PacketDistributor.sendToServer(new PullStuckArrowPayload(target.entity.getId(), target.slot, target.part));
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.keyUse.isDown()) useHeld = false;
        if (minecraft.player == null || minecraft.screen != null) hoveredWorld = null;
        if (!(minecraft.screen instanceof InventoryScreen)) hoveredInventory = null;
    }

    private static List<Component> tooltip(Candidate candidate, String action) {
        StuckArrowSlots.Slot slot = candidate.entity.hasData(ModAttachments.STUCK_ARROWS)
                ? candidate.entity.getData(ModAttachments.STUCK_ARROWS).get(candidate.slot) : null;
        Component name = slot != null && slot.recoverable() ? slot.stack().getHoverName() : Items.ARROW.getDescription();
        return List.of(name, Component.translatable(action).withStyle(ChatFormatting.GRAY));
    }

    private static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Candidate target = hoveredWorld;
        if (target == null || minecraft.screen != null || minecraft.options.hideGui) return;
        GuiGraphics graphics = event.getGuiGraphics();
        graphics.renderComponentTooltip(minecraft.font, tooltip(target, "tooltip.spicedcider.stuck_arrow.pull"),
                graphics.guiWidth() / 2, graphics.guiHeight() / 2);
    }

    private static void onScreenRender(ScreenEvent.Render.Post event) {
        Candidate target = hoveredInventory;
        if (target == null || !(event.getScreen() instanceof InventoryScreen)) return;
        event.getGuiGraphics().renderComponentTooltip(Minecraft.getInstance().font,
                tooltip(target, "tooltip.spicedcider.stuck_arrow.pull_inventory"), event.getMouseX(), event.getMouseY());
    }

    private static boolean overModel(InventoryScreen screen, double mouseX, double mouseY) {
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();
        return mouseX >= left + MODEL_LEFT && mouseX < left + MODEL_RIGHT && mouseY >= top + MODEL_TOP && mouseY < top + MODEL_BOTTOM;
    }

    private static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen screen) || event.getButton() != 0) return;
        Candidate target = hoveredInventory;
        if (target != null && Minecraft.getInstance().player != null) {
            PacketDistributor.sendToServer(new PullStuckArrowPayload(target.entity.getId(), target.slot, target.part));
            hoveredInventory = null;
            event.setCanceled(true);
            return;
        }
        if (rotationEnabled() && screen.getMenu().getCarried().isEmpty() && overModel(screen, event.getMouseX(), event.getMouseY())) {
            dragging = true;
            event.setCanceled(true);
        }
    }

    private static void onMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        if (!dragging || !(event.getScreen() instanceof InventoryScreen)) return;
        modelYaw = (modelYaw + (float) event.getDragX() * DRAG_DEGREES_PER_PIXEL) % 360.0F;
        event.setCanceled(true);
    }

    private static void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        if (dragging && event.getButton() == 0) {
            dragging = false;
            event.setCanceled(true);
        }
    }

    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof InventoryScreen && !(event.getCurrentScreen() instanceof InventoryScreen)) {
            modelYaw = 0.0F;
            dragging = false;
        }
    }
}
