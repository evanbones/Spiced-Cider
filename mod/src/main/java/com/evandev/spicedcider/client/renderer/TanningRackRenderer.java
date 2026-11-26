package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.SpicedCider;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class TanningRackRenderer implements BlockEntityRenderer<TanningRackBlockEntity> {
    private static final float CANVAS = 32.0F;
    private static final float TOP = 28.0F;
    private static final float ITEM_SCALE = 0.75F;
    private static final float ROPE_LENGTH = 4.0F / 16.0F;
    private static final float ROPE_WIDTH = 2.0F / 16.0F;
    private static final float ROPE_OVERLAP = 1.0F / 16.0F;
    private static final ResourceLocation ROPE = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/drying_rack_rope");

    public TanningRackRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TanningRackBlockEntity rack, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (rack.isEmpty() || rack.getLevel() == null) return;
        DryingRecipe recipe = rack.recipe();
        if (recipe == null) return;
        if (recipe.rack().isEmpty()) {
            renderItems(rack, recipe, poseStack, buffers);
            return;
        }
        DryingRecipe.Rack display = recipe.rack().get();
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

    private void renderItems(TanningRackBlockEntity rack, DryingRecipe recipe, PoseStack poseStack, MultiBufferSource buffers) {
        boolean finished = rack.stage() == TanningRackBlockEntity.Stage.FINISHED;
        ItemStack shown = (finished ? recipe.result() : rack.hung()).copyWithCount(1);
        if (shown.isEmpty()) return;
        int packedLight = LevelRenderer.getLightColor(rack.getLevel(), rack.getBlockPos().above());
        float ropeTop = TOP / 16.0F;
        float ropeBottom = ropeTop - ROPE_LENGTH;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rack.getBlockState().getValue(TanningRackBlock.FACING).toYRot()));
        renderRope(poseStack, buffers, ropeTop, ropeBottom - ROPE_OVERLAP, packedLight);
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        float fit = itemRenderer.getModel(shown, rack.getLevel(), null, 0).isGui3d() ? 2.0F : 1.0F;
        poseStack.translate(0.0F, ropeBottom - ITEM_SCALE / 2.0F, 0.0F);
        poseStack.scale(ITEM_SCALE * fit, ITEM_SCALE * fit, ITEM_SCALE * fit);
        itemRenderer.renderStatic(shown, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffers, rack.getLevel(), (int) rack.getBlockPos().asLong());
        poseStack.popPose();
    }

    private static void renderRope(PoseStack poseStack, MultiBufferSource buffers, float top, float bottom, int light) {
        TextureAtlasSprite sprite = ropeSprite();
        VertexConsumer consumer = buffers.getBuffer(Sheets.cutoutBlockSheet());
        PoseStack.Pose pose = poseStack.last();
        float half = ROPE_WIDTH / 2.0F;
        float u0 = sprite.getU(7.0F / 16.0F);
        float u1 = sprite.getU(9.0F / 16.0F);
        float v0 = sprite.getV(0.0F);
        float v1 = sprite.getV(Math.min(1.0F, top - bottom));
        for (int side = 0; side < 2; side++) {
            float normal = side == 0 ? 1.0F : -1.0F;
            ropeVertex(consumer, pose, -half * normal, top, 0.0F, u0, v0, light, 0.0F, normal);
            ropeVertex(consumer, pose, -half * normal, bottom, 0.0F, u0, v1, light, 0.0F, normal);
            ropeVertex(consumer, pose, half * normal, bottom, 0.0F, u1, v1, light, 0.0F, normal);
            ropeVertex(consumer, pose, half * normal, top, 0.0F, u1, v0, light, 0.0F, normal);
            ropeVertex(consumer, pose, 0.0F, top, half * normal, u0, v0, light, normal, 0.0F);
            ropeVertex(consumer, pose, 0.0F, bottom, half * normal, u0, v1, light, normal, 0.0F);
            ropeVertex(consumer, pose, 0.0F, bottom, -half * normal, u1, v1, light, normal, 0.0F);
            ropeVertex(consumer, pose, 0.0F, top, -half * normal, u1, v0, light, normal, 0.0F);
        }
    }

    private static TextureAtlasSprite ropeSprite() {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ROPE);
    }

    private static void ropeVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int light, float normalX, float normalZ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normalX, 0.0F, normalZ);
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
