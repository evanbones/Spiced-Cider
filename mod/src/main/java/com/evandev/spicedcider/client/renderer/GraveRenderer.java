package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.content.block.GravestoneBlock;
import com.evandev.spicedcider.content.block.entity.GraveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class GraveRenderer implements BlockEntityRenderer<GraveBlockEntity> {
    private static final float TEXT_SCALE = 1.0F / 7.0F;
    private static final int LINE_HEIGHT = 7;
    private static final int MAX_WIDTH = 120;
    private static final int TEXT_COLOR = SignRenderer.getDarkColor(new SignText());
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final Font font;
    private final SkullModelBase head;

    public GraveRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
        this.head = SkullBlockRenderer.createSkullRenderers(context.getModelSet()).get(SkullBlock.Types.PLAYER);
    }

    @Override
    public void render(GraveBlockEntity grave, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        BlockState state = grave.getBlockState();
        Direction facing = state.hasProperty(GravestoneBlock.FACING) ? state.getValue(GravestoneBlock.FACING) : Direction.NORTH;
        int rotation = switch (facing) {
            case WEST -> 90;
            case SOUTH -> 180;
            case EAST -> 270;
            default -> 0;
        };

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        renderText(grave, poseStack, buffers, light);
        renderHead(grave, poseStack, buffers, light);
        poseStack.popPose();
    }

    private void renderText(GraveBlockEntity grave, PoseStack poseStack, MultiBufferSource buffers, int light) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.scale(0.0625F, -0.0625F, 0.0625F);
        poseStack.translate(-8, -14, -11.95);
        poseStack.scale(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);

        String date = "";
        String time = "";
        if (grave.getDeathTime() > 0) {
            Instant instant = Instant.ofEpochMilli(grave.getDeathTime());
            date = DATE.withZone(ZoneId.systemDefault()).format(instant);
            time = TIME.withZone(ZoneId.systemDefault()).format(instant);
        }
        String[] lines = {grave.getOwnerName(), date, time};
        for (int i = 0; i < lines.length; i++) {
            String line = this.font.plainSubstrByWidth(lines[i], MAX_WIDTH);
            float x = -this.font.width(line) / 2.0F;
            float y = (i + 1) * LINE_HEIGHT * 2;
            this.font.drawInBatch(line, x, y, TEXT_COLOR, false, poseStack.last().pose(), buffers, Font.DisplayMode.POLYGON_OFFSET, 0, light);
        }
        poseStack.popPose();
    }

    private void renderHead(GraveBlockEntity grave, PoseStack poseStack, MultiBufferSource buffers, int light) {
        BlockPos pos = grave.getBlockPos();
        RandomSource random = RandomSource.create(pos.asLong());
        float yaw = random.nextFloat() * 70.0F - 35.0F;
        float pitch = random.nextFloat() * -30.0F;

        poseStack.pushPose();
        poseStack.translate(0.0F, -0.0625F, 0.0F);
        poseStack.translate(0.5F, 0.0F, 0.475F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.head.setupAnim(0.0F, yaw, pitch);
        this.head.renderToBuffer(poseStack, buffers.getBuffer(SkullBlockRenderer.getRenderType(SkullBlock.Types.PLAYER, grave.getOwner())), light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
