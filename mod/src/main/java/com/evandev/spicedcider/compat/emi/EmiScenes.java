package com.evandev.spicedcider.compat.emi;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EmiScenes {

    public static long millis() {
        return System.currentTimeMillis();
    }

    public static void beginIso(GuiGraphics graphics, float centerX, float centerY, float scale) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(centerX, centerY, 100);
        pose.scale(scale, -scale, scale);
        pose.mulPose(Axis.XP.rotationDegrees(30));
        pose.mulPose(Axis.YP.rotationDegrees(45));
        pose.translate(-0.5f, -0.5f, -0.5f);
        Lighting.setupFor3DItems();
    }

    public static void endIso(GuiGraphics graphics) {
        graphics.flush();
        graphics.pose().popPose();
    }

    public static void block(GuiGraphics graphics, BlockState state, float x, float y, float z) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, z);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, pose, graphics.bufferSource(),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        pose.popPose();
    }

    public static void item(GuiGraphics graphics, ItemStack stack, float x, float y, float z, float scale, float yaw, float roll) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, z);
        pose.mulPose(Axis.YP.rotationDegrees(yaw));
        pose.mulPose(Axis.ZP.rotationDegrees(roll));
        pose.scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, pose, graphics.bufferSource(), Minecraft.getInstance().level, 0);
        pose.popPose();
    }

    public static void fluid(GuiGraphics graphics, Fluid fluid, float y0, float y1) {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());
        int tint = extensions.getTintColor();
        int a = 220;
        int r = tint >> 16 & 255;
        int g = tint >> 8 & 255;
        int b = tint & 255;
        VertexConsumer buffer = graphics.bufferSource().getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose matrix = graphics.pose().last();
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float vSide = sprite.getV(y1 - y0);
        quad(buffer, matrix, r, g, b, a, new float[][]{{0, y1, 0}, {0, y1, 1}, {1, y1, 1}, {1, y1, 0}}, u0, v0, u1, v1, 0, 1, 0);
        quad(buffer, matrix, r, g, b, a, new float[][]{{0, y1, 1}, {0, y0, 1}, {1, y0, 1}, {1, y1, 1}}, u0, v0, u1, vSide, 0, 0, 1);
        quad(buffer, matrix, r, g, b, a, new float[][]{{1, y1, 1}, {1, y0, 1}, {1, y0, 0}, {1, y1, 0}}, u0, v0, u1, vSide, 1, 0, 0);
        quad(buffer, matrix, r, g, b, a, new float[][]{{1, y1, 0}, {1, y0, 0}, {0, y0, 0}, {0, y1, 0}}, u0, v0, u1, vSide, 0, 0, -1);
        quad(buffer, matrix, r, g, b, a, new float[][]{{0, y1, 0}, {0, y0, 0}, {0, y0, 1}, {0, y1, 1}}, u0, v0, u1, vSide, -1, 0, 0);
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose matrix, int r, int g, int b, int a, float[][] corners,
                             float u0, float v0, float u1, float v1, float nx, float ny, float nz) {
        float[][] uvs = {{u0, v0}, {u0, v1}, {u1, v1}, {u1, v0}};
        for (int i = 0; i < 4; i++) {
            buffer.addVertex(matrix, corners[i][0], corners[i][1], corners[i][2])
                    .setColor(r, g, b, a)
                    .setUv(uvs[i][0], uvs[i][1])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(matrix, nx, ny, nz);
        }
    }

    public static void entity(GuiGraphics graphics, LivingEntity entity, float x, float y, float scale, float bodyYaw, float pitch) {
        float bodyRot = entity.yBodyRot;
        float yRot = entity.getYRot();
        float xRot = entity.getXRot();
        float headRotO = entity.yHeadRotO;
        float headRot = entity.yHeadRot;
        entity.yBodyRot = 180 + bodyYaw;
        entity.yBodyRotO = entity.yBodyRot;
        entity.setYRot(180 + bodyYaw);
        entity.setXRot(0);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateX(pitch * (float) (Math.PI / 180));
        pose.mul(camera);
        InventoryScreen.renderEntityInInventory(graphics, x, y, scale / entity.getScale(), new Vector3f(0, entity.getBbHeight() / 2, 0), pose, camera, entity);
        entity.yBodyRot = bodyRot;
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        entity.yHeadRotO = headRotO;
        entity.yHeadRot = headRot;
    }
}
