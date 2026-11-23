package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.content.block.TanningRackBlock;
import com.evandev.spicedcider.content.block.entity.TanningRackBlockEntity;
import com.evandev.spicedcider.recipe.DryingRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;

public class TanningRackRenderer implements BlockEntityRenderer<TanningRackBlockEntity> {
    private static final float CANVAS = 32.0F;
    private static final float TOP = 28.0F;

    public TanningRackRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TanningRackBlockEntity rack, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (rack.isEmpty() || rack.getLevel() == null) return;
        DryingRecipe recipe = rack.recipe();
        if (recipe == null) return;
        DryingRecipe.Rack display = recipe.rack();
        ResourceLocation texture = switch (rack.stage()) {
            case HUNG -> display.hung();
            case DRYING -> display.drying();
            case FINISHED -> display.finished();
        };
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        int packedLight = LevelRenderer.getLightColor(rack.getLevel(), rack.getBlockPos().above());
        int cells = rack.stage() == TanningRackBlockEntity.Stage.HUNG ? Math.min(rack.hung().getCount(), recipe.count()) : recipe.count();
        int cols = recipe.count() > 1 && recipe.count() % 2 == 0 ? 2 : 1;
        int rows = Math.max(1, recipe.count() / cols);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rack.getBlockState().getValue(TanningRackBlock.FACING).toYRot()));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        VertexConsumer consumer = buffers.getBuffer(Sheets.cutoutBlockSheet());
        PoseStack.Pose pose = poseStack.last();
        float w = display.width();
        float h = display.height();
        float xMin = (16.0F - w) / 2.0F;
        float xMax = xMin + w;
        for (int i = 0; i < cells; i++) {
            int col = i % cols;
            int row = i / cols;
            float ua = w * col / cols;
            float ub = w * (col + 1) / cols;
            float va = h * row / rows;
            float vb = h * (row + 1) / rows;
            float yTop = (TOP - va) / 16.0F;
            float yBottom = (TOP - vb) / 16.0F;
            float u0 = sprite.getU(ua / CANVAS);
            float u1 = sprite.getU(ub / CANVAS);
            float v0 = sprite.getV(va / CANVAS);
            float v1 = sprite.getV(vb / CANVAS);
            float nLeft = (xMax - ua) / 16.0F;
            float nRight = (xMax - ub) / 16.0F;
            vertex(consumer, pose, nLeft, yTop, u0, v0, packedLight, -1.0F);
            vertex(consumer, pose, nLeft, yBottom, u0, v1, packedLight, -1.0F);
            vertex(consumer, pose, nRight, yBottom, u1, v1, packedLight, -1.0F);
            vertex(consumer, pose, nRight, yTop, u1, v0, packedLight, -1.0F);
            float sLeft = (xMin + ua) / 16.0F;
            float sRight = (xMin + ub) / 16.0F;
            vertex(consumer, pose, sLeft, yTop, u0, v0, packedLight, 1.0F);
            vertex(consumer, pose, sLeft, yBottom, u0, v1, packedLight, 1.0F);
            vertex(consumer, pose, sRight, yBottom, u1, v1, packedLight, 1.0F);
            vertex(consumer, pose, sRight, yTop, u1, v0, packedLight, 1.0F);
        }
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int light, float normalZ) {
        consumer.addVertex(pose, x, y, 0.5F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 0.0F, normalZ);
    }

    @Override
    public AABB getRenderBoundingBox(TanningRackBlockEntity rack) {
        return new AABB(rack.getBlockPos()).expandTowards(0.0, 1.0, 0.0);
    }
}
