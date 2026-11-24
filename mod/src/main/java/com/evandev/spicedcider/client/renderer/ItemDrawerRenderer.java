package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.drawer.ItemDrawerBlock;
import com.evandev.spicedcider.content.drawer.ItemDrawerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ItemDrawerRenderer implements BlockEntityRenderer<ItemDrawerBlockEntity> {

    private static final ResourceLocation INDICATOR = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/item_drawer_indicator");
    private static final double LABEL_DISTANCE = 25;
    private static final double COUNT_DISTANCE = 10;
    private static final double COUNT_FADE_DISTANCE = 4;
    private static final Matrix3f LIGHT_3D = new Matrix3f().rotationYXZ(0.36f, -0.36f, -0.014f);
    private static final Matrix3f LIGHT_FLAT = new Matrix3f().rotationYXZ(0, -0.785398f, 0);
    private static final float ICON_MIN = 5.5f;
    private static final float ICON_MAX = 10.5f;
    private static final float BAR_MIN_X = 3;
    private static final float BAR_MAX_X = 13;
    private static final float BAR_MIN_Y = 3;
    private static final float BAR_MAX_Y = 4;
    private static final int BAR_STEPS = 10;

    private final Font font;

    public ItemDrawerRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(ItemDrawerBlockEntity drawer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Player player = Minecraft.getInstance().player;
        if (player == null || drawer.getLevel() == null || drawer.isEmpty()) return;
        BlockState state = drawer.getBlockState();
        if (!state.hasProperty(ItemDrawerBlock.FACING)) return;
        Direction facing = state.getValue(ItemDrawerBlock.FACING);
        BlockPos pos = drawer.getBlockPos();
        if (behind(player.blockPosition(), pos, facing)) return;

        double distance = Math.sqrt(pos.distToCenterSqr(player.position()));
        if (distance > LABEL_DISTANCE) return;

        int light = LevelRenderer.getLightColor(drawer.getLevel(), pos.relative(facing));

        poseStack.pushPose();
        poseStack.translate(0.5f, 0, 0.5f);
        poseStack.mulPose(new Matrix4f().rotateYXZ(rotation(facing), 0, 0));
        poseStack.translate(-0.5f, 0, -0.5f);

        renderItem(drawer.getItem(), poseStack, buffer, light, packedOverlay, facing);
        renderIndicator(drawer, poseStack, buffer, light, packedOverlay);
        if (distance < COUNT_DISTANCE) {
            float alpha = distance > COUNT_FADE_DISTANCE ? (float) Math.max(1 - (distance - 4) / 6, 0.05) : 1;
            renderCount(format(drawer.getCount()), poseStack, buffer, light, alpha);
        }

        poseStack.popPose();
    }

    private void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, Direction facing) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        float scale = (ICON_MAX - ICON_MIN) / 16;
        float moveX = ICON_MIN + 8 * scale;
        float moveY = 16 - ICON_MAX + 8 * scale;

        poseStack.pushPose();
        poseStack.translate(moveX / 16, 1 - moveY / 16, 1.0025f);
        poseStack.mulPose(new Matrix4f().scale(scale, scale, 0.001f));

        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        if (model.isGui3d()) {
            poseStack.last().normal().rotateYXZ(-rotation(facing), 0, 0).mul(LIGHT_3D);
        } else {
            poseStack.last().normal().mul(LIGHT_FLAT);
        }
        ItemDisplayContext context = model.isCustomRenderer() ? ItemDisplayContext.FIXED : ItemDisplayContext.GUI;
        itemRenderer.render(stack, context, false, poseStack, buffer, light, overlay, model);

        poseStack.popPose();
    }

    private void renderCount(String text, PoseStack poseStack, MultiBufferSource buffer, int light, float alpha) {
        int width = font.width(text);
        poseStack.pushPose();
        poseStack.translate(0.5f, BAR_MAX_Y / 16, 1.01f);
        poseStack.scale(1 / 128f, -1 / 128f, 1);
        int color = (int) (255 * alpha) << 24 | 0xFFFFFF;
        font.drawInBatch(text, -width / 2f, 0, color, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        poseStack.popPose();
    }

    private void renderIndicator(ItemDrawerBlockEntity drawer, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        int capacity = drawer.getCapacity();
        if (capacity <= 0) return;
        float fill = (float) (BAR_STEPS * drawer.getCount() / capacity) / BAR_STEPS;
        if (fill <= 0) return;

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(INDICATOR);
        float unit = 1 / 16f;
        float divU = unit * (sprite.getU1() - sprite.getU0());
        float divV = unit * (sprite.getV1() - sprite.getV0());

        float x1 = BAR_MIN_X * unit;
        float x2 = x1 + (BAR_MAX_X - BAR_MIN_X) * unit * fill;
        float y1 = BAR_MIN_Y * unit;
        float y2 = BAR_MAX_Y * unit;
        float z = 1 + 0.02f * unit;
        float u1 = sprite.getU0() + BAR_MIN_X * divU;
        float u2 = u1 + (BAR_MAX_X - BAR_MIN_X) * divU * fill;
        float v1 = sprite.getV1() - BAR_MIN_Y * divV;
        float v2 = sprite.getV1() - BAR_MAX_Y * divV;

        Matrix4f matrix = poseStack.last().pose();
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        vertex(consumer, matrix, pose, x2, y1, z, u2, v1, light, overlay);
        vertex(consumer, matrix, pose, x2, y2, z, u2, v2, light, overlay);
        vertex(consumer, matrix, pose, x1, y2, z, u1, v2, light, overlay);
        vertex(consumer, matrix, pose, x1, y1, z, u1, v1, light, overlay);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, PoseStack.Pose pose, float x, float y, float z, float u, float v, int light, int overlay) {
        consumer.addVertex(matrix, x, y, z).setColor(1f, 1f, 1f, 1f).setUv(u, v).setOverlay(overlay).setLight(light).setNormal(pose, 0, 0, 1);
    }

    private static boolean behind(BlockPos player, BlockPos pos, Direction facing) {
        return switch (facing) {
            case NORTH -> player.getZ() > pos.getZ();
            case SOUTH -> player.getZ() < pos.getZ();
            case WEST -> player.getX() > pos.getX();
            case EAST -> player.getX() < pos.getX();
            default -> false;
        };
    }

    private static float rotation(Direction facing) {
        return switch (facing) {
            case NORTH -> (float) Math.PI;
            case WEST -> (float) (Math.PI * 1.5);
            case EAST -> (float) (Math.PI * 0.5);
            default -> 0;
        };
    }

    public static String format(int count) {
        if (count >= 100000) return String.format("%.0fK", count / 1000f);
        if (count >= 10000) return String.format("%.1fK", count / 1000f);
        return String.valueOf(count);
    }
}
