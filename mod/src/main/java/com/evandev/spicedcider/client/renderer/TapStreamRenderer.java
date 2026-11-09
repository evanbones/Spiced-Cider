package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.compat.nomansland.tap.TapFaucetBridge;
import com.evandev.spicedcider.compat.nomansland.tap.TapFluidHolder;
import com.evandev.spicedcider.compat.nomansland.tap.TapFluidState;
import com.farcr.nomansland.common.block.tap.TapBlock;
import com.farcr.nomansland.common.blockentity.TapBlockEntity;
import com.farcr.nomansland.common.registry.NMLBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class TapStreamRenderer implements BlockEntityRenderer<TapBlockEntity> {
    private static final float HALF_WIDTH = 1.0F / 16.0F;
    private static final int ALPHA = 0xE6;

    public TapStreamRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(NMLBlockEntities.TAP.get(), TapStreamRenderer::new);
    }

    @Override
    public void render(TapBlockEntity tap, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (!(tap instanceof TapFluidHolder holder) || tap.getLevel() == null) return;
        TapFluidState fluids = holder.spicedcider$fluids();
        SoftFluidStack stream = fluids.stream();
        float length = fluids.streamLength();
        BlockState state = tap.getBlockState();
        if (stream == null || stream.isEmpty() || length < 0.01F || !state.hasProperty(TapBlock.CLOSED) || state.getValue(TapBlock.CLOSED)) return;

        SoftFluid fluid = stream.fluid();
        ResourceLocation texture = fluid.getFlowingTexture();
        if (texture == null) texture = fluid.getStillTexture();
        if (texture == null) return;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        int color = (ALPHA << 24) | (stream.getFlowingColor(tap.getLevel(), tap.getBlockPos()) & 0xFFFFFF);
        int luminosity = fluid.getLuminosity();
        if (luminosity > 0) {
            light = LightTexture.pack(Math.max(LightTexture.block(light), Math.min(15, luminosity)), LightTexture.sky(light));
        }

        float cx = 0.5F;
        float cz = 0.5F;
        switch (state.getValue(TapBlock.FACING)) {
            case NORTH -> cz = 11.0F / 16.0F;
            case SOUTH -> cz = 5.0F / 16.0F;
            case EAST -> cx = 5.0F / 16.0F;
            case WEST -> cx = 11.0F / 16.0F;
            default -> {
            }
        }

        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();
        float x0 = cx - HALF_WIDTH;
        float x1 = cx + HALF_WIDTH;
        float z0 = cz - HALF_WIDTH;
        float z1 = cz + HALF_WIDTH;
        float u0 = sprite.getU(0.4375F);
        float u1 = sprite.getU(0.5625F);
        float top = TapFaucetBridge.SPOUT_HEIGHT;
        float bottom = top - length;
        for (float y1 = top; y1 > bottom + 0.001F; y1 -= 1.0F) {
            float y0 = Math.max(bottom, y1 - 1.0F);
            float v0 = sprite.getV(0.0F);
            float v1 = sprite.getV(y1 - y0);
            quad(consumer, pose, x0, z0, x1, z0, y0, y1, u0, u1, v0, v1, color, light, 0, -1);
            quad(consumer, pose, x1, z1, x0, z1, y0, y1, u0, u1, v0, v1, color, light, 0, 1);
            quad(consumer, pose, x0, z1, x0, z0, y0, y1, u0, u1, v0, v1, color, light, -1, 0);
            quad(consumer, pose, x1, z0, x1, z1, y0, y1, u0, u1, v0, v1, color, light, 1, 0);
        }
    }

    private static void quad(VertexConsumer consumer, PoseStack.Pose pose, float xa, float za, float xb, float zb, float y0, float y1, float u0, float u1, float v0, float v1, int color, int light, int nx, int nz) {
        vertex(consumer, pose, xa, y1, za, u0, v0, color, light, nx, nz);
        vertex(consumer, pose, xa, y0, za, u0, v1, color, light, nx, nz);
        vertex(consumer, pose, xb, y0, zb, u1, v1, color, light, nx, nz);
        vertex(consumer, pose, xb, y1, zb, u1, v0, color, light, nx, nz);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int color, int light, int nx, int nz) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, 0, nz);
    }

    @Override
    public AABB getRenderBoundingBox(TapBlockEntity tap) {
        return new AABB(tap.getBlockPos()).expandTowards(0, -4, 0);
    }
}
