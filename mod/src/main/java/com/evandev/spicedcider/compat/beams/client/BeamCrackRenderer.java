package com.evandev.spicedcider.compat.beams.client;

import com.evandev.spicedcider.mixin.beams.client.BeamBlockEntityRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.syl.beams.blockentities.BeamBlockEntity;
import com.syl.beams.client.renderer.beam.BeamPrismRenderer;
import com.syl.beams.client.renderer.beam.BeamTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class BeamCrackRenderer {
    private static BeamBlockEntity.BeamLink link;
    private static int stage = -1;

    public static void set(BeamBlockEntity.BeamLink target, int progressStage) {
        link = target;
        stage = progressStage;
    }

    public static void clear() {
        link = null;
        stage = -1;
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        BeamBlockEntity.BeamLink target = link;
        if (target == null || stage < 0 || target.otherVec == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        TextureAtlasSprite sprite = BeamTextures.plankSprite(target.woodType);
        if (sprite == null) return;
        Vec3 start = target.isStart ? target.selfVec : target.otherVec;
        Vec3 end = target.isStart ? target.otherVec : target.selfVec;
        Vec3 jitter = BeamBlockEntityRendererAccessor.spicedcider$stableJitter(start, end, target.woodType);
        Vec3 cam = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-cam.x, -cam.y, -cam.z);
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().crumblingBufferSource();
        VertexConsumer consumer = new SheetedDecalTextureGenerator(buffers.getBuffer(ModelBakery.DESTROY_TYPES.get(Math.min(stage, 9))), poseStack.last(), 1.0F);
        BeamPrismRenderer.render(poseStack, consumer, start.add(jitter), end.add(jitter), Vec3.ZERO, mc.level, sprite, 255, 255, 255, 255);
        buffers.endBatch();
        poseStack.popPose();
    }
}
