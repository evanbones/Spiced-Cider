package com.evandev.spicedcider.compat.beams.client;

import com.evandev.spicedcider.compat.beams.BeamSnapping;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.syl.beams.BeamsConfig;
import com.syl.beams.attachment.BeamPlacementData;
import com.syl.beams.attachment.BeamsAttachments;
import com.syl.beams.client.renderer.beam.BeamPrismRenderer;
import com.syl.beams.client.renderer.beam.BeamTextures;
import com.syl.beams.items.BeamItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Optional;

public final class BeamPreviewRenderer {
    private static final double CORNER_HALF = 0.125;
    private static final double POINT_HALF = 0.03125;
    private static final double SELECTED_HALF = 0.0625;
    private static final double LIFT = 0.003;
    private static final double SAME_POINT = 0.01;

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null || !(player.getMainHandItem().getItem() instanceof BeamItem)) return;

        HitResult hit = BeamItem.raytraceSkippingAnchors(mc.level, player);
        BlockHitResult blockHit = hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult bhr ? bhr : null;
        Optional<BeamPlacementData> pending = player.getData(BeamsAttachments.BEAM_PLACEMENT);

        Vec3 cam = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();

        if (blockHit != null && BeamSnapping.gridEnabled()) {
            renderGrid(poseStack, buffers.getBuffer(RenderType.lines()), blockHit);
            buffers.endBatch(RenderType.lines());
        }

        if (pending.isPresent()) {
            BeamPlacementData start = pending.get();
            Vec3 startWorld = start.toWorldVec3();
            Vec3 target = hit.getLocation();
            if (blockHit != null) {
                int[] uvd = BeamPlacementData.snapHit(blockHit.getLocation(), blockHit.getBlockPos(), blockHit.getDirection());
                target = BeamPlacementData.snapToWorld(blockHit.getBlockPos(), blockHit.getDirection(), uvd[0], uvd[1], uvd[2]);
            }
            if (startWorld.distanceTo(target) < SAME_POINT) {
                renderCorner(poseStack, buffers, start, startWorld);
            }
        }

        poseStack.popPose();
    }

    private static void renderCorner(PoseStack poseStack, MultiBufferSource.BufferSource buffers, BeamPlacementData start, Vec3 startWorld) {
        TextureAtlasSprite sprite = BeamTextures.plankSprite(start.woodType());
        if (sprite == null) return;
        Vec3 normal = Vec3.atLowerCornerOf(start.face().getNormal()).scale(CORNER_HALF);
        boolean ghost = BeamsConfig.GHOST_PREVIEW.get();
        RenderType renderType = ghost ? RenderType.translucent() : RenderType.cutoutMipped();
        BeamPrismRenderer.render(poseStack, buffers.getBuffer(renderType), startWorld.subtract(normal), startWorld.add(normal),
                Vec3.ZERO, Minecraft.getInstance().level, sprite, 255, 255, 255, ghost ? 100 : 255);
        buffers.endBatch(renderType);
    }

    private static void renderGrid(PoseStack poseStack, VertexConsumer lines, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        Direction face = hit.getDirection();
        int[] selected = BeamPlacementData.snapHit(hit.getLocation(), pos, face);
        Vec3 lift = Vec3.atLowerCornerOf(face.getNormal()).scale(LIFT);
        for (int u : BeamSnapping.GRID) {
            for (int v : BeamSnapping.GRID) {
                boolean chosen = u == selected[0] && v == selected[1];
                Vec3 point = BeamPlacementData.snapToWorld(pos, face, u, v, selected[2]).add(lift);
                double half = chosen ? SELECTED_HALF : POINT_HALF;
                AABB box = flatBox(point, face.getAxis(), half);
                float shade = chosen ? 1.0F : 0.6F;
                LevelRenderer.renderLineBox(poseStack, lines, box, shade, shade, shade, chosen ? 0.9F : 0.45F);
            }
        }
    }

    private static AABB flatBox(Vec3 c, Direction.Axis axis, double half) {
        double hx = axis == Direction.Axis.X ? 0 : half;
        double hy = axis == Direction.Axis.Y ? 0 : half;
        double hz = axis == Direction.Axis.Z ? 0 : half;
        return new AABB(c.x - hx, c.y - hy, c.z - hz, c.x + hx, c.y + hy, c.z + hz);
    }
}
