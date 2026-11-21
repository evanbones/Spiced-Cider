package com.evandev.spicedcider.client.renderer;

import com.evandev.spicedcider.content.casting.CrucibleBlock;
import com.evandev.spicedcider.content.casting.CrucibleBlockEntity;
import com.evandev.spicedcider.content.casting.MouldBlock;
import com.evandev.spicedcider.content.casting.MouldBlockEntity;
import com.evandev.spicedcider.content.casting.MouldShape;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlock;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.material.Fluids;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class CastingRenderers {
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CRUCIBLE.get(), Crucible::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOULD.get(), Mould::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SMELTERY.get(), Smeltery::new);
    }

    public static class Mould implements BlockEntityRenderer<MouldBlockEntity> {
        private final ItemRenderer items;

        public Mould(BlockEntityRendererProvider.Context context) {
            this.items = context.getItemRenderer();
        }

        @Override
        public void render(MouldBlockEntity mould, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.0F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - mould.getBlockState().getValue(MouldBlock.FACING).toYRot()));
            poseStack.translate(-0.5F, 0.0F, -0.5F);
            float cooling = mould.coolingFraction(partialTick);
            int itemAlpha = Math.round(255.0F * cooling);
            int fluidAlpha = cooling > 0.75F ? Math.round(255.0F * (1.0F - cooling) * 4.0F) : 255;
            ItemStack result = mould.getResult();
            MultiBufferSource itemBuffers = buffers;
            if (result.isEmpty() && itemAlpha > 0) {
                result = mould.coolingOutput();
                itemBuffers = new CoolingItemBuffer(buffers, itemAlpha, fluidAlpha);
            }
            if (!result.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0.5F, (MouldBlock.RIM - 0.55F) / 16.0F, 0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.scale(0.75F, 0.75F, 0.08F);
                this.items.renderStatic(result, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, itemBuffers, mould.getLevel(), (int) mould.getBlockPos().asLong());
                poseStack.popPose();
            }
            FluidStack fluid = mould.getFluid();
            if (!fluid.isEmpty() && fluidAlpha > 0) {
                float height = MouldBlock.FLOOR + 0.1F + (MouldBlock.RIM - MouldBlock.FLOOR - 0.5F) * mould.displayFill(partialTick);
                for (MouldShape.Cavity cavity : mould.shape().cavities()) {
                    surface(poseStack, buffers, fluid, cavity.minX(), cavity.minZ(), cavity.maxX(), cavity.maxZ(), height, light, 1.0F, fluidAlpha);
                }
            }
            poseStack.popPose();
        }
    }

    public static class Smeltery implements BlockEntityRenderer<SmelteryBlockEntity> {
        public static final float WINDOW_LEFT = 10.0F;
        public static final float WINDOW_RIGHT = 22.0F;
        public static final float WINDOW_TOP = 6.0F;
        public static final float WINDOW_BOTTOM = 19.0F;
        public static final float FIRE_LEFT = 11.0F;
        public static final float FIRE_RIGHT = 21.0F;
        public static final float FIRE_TOP = 23.0F;
        public static final float FIRE_BOTTOM = 27.0F;

        public Smeltery(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(SmelteryBlockEntity smeltery, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.0F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - smeltery.getBlockState().getValue(SmelteryBlock.FACING).toYRot()));
            poseStack.translate(-0.5F, 0.0F, -0.5F);
            int front = smeltery.getLevel() == null ? light : LevelRenderer.getLightColor(smeltery.getLevel(), smeltery.getBlockPos().relative(smeltery.getBlockState().getValue(SmelteryBlock.FACING)));
            float level = WINDOW_BOTTOM;
            int amount = 0;
            for (FluidStack metal : smeltery.getMetals()) {
                amount += metal.getAmount();
                float top = WINDOW_BOTTOM - (WINDOW_BOTTOM - WINDOW_TOP) * Math.min(1.0F, (float) amount / SmelteryBlockEntity.METAL_CAPACITY);
                if (top < level - 0.01F) {
                    front(poseStack, buffers, metal, WINDOW_LEFT, top, WINDOW_RIGHT, level, front);
                    level = top;
                }
            }
            if (smeltery.isActive()) {
                front(poseStack, buffers, new FluidStack(Fluids.LAVA, 1000), FIRE_LEFT, FIRE_TOP, FIRE_RIGHT, FIRE_BOTTOM, front);
            }
            poseStack.popPose();
        }

        @Override
        public AABB getRenderBoundingBox(SmelteryBlockEntity smeltery) {
            return new AABB(smeltery.getBlockPos()).inflate(2.0);
        }
    }

    private static void front(PoseStack poseStack, MultiBufferSource buffers, FluidStack fluid, float left, float top, float right, float bottom, int light) {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(fluid));
        int color = extensions.getTintColor(fluid) | 0xFF000000;
        int glow = Math.max(fluid.getFluid().getFluidType().getLightLevel(fluid), 10);
        int packed = LightTexture.pack(Math.max(LightTexture.block(light), glow), LightTexture.sky(light));
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();
        float x0 = 2.0F - right / 16.0F;
        float x1 = 2.0F - left / 16.0F;
        float y0 = 2.0F - bottom / 16.0F;
        float y1 = 2.0F - top / 16.0F;
        float z = -0.002F;
        float u0 = sprite.getU((left % 16.0F) / 16.0F);
        float u1 = sprite.getU(Math.min(1.0F, (left % 16.0F + right - left) / 16.0F));
        float v0 = sprite.getV((top % 16.0F) / 16.0F);
        float v1 = sprite.getV(Math.min(1.0F, (top % 16.0F + bottom - top) / 16.0F));
        consumer.addVertex(pose, x1, y1, z).setColor(color).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packed).setNormal(pose, 0, 0, -1);
        consumer.addVertex(pose, x1, y0, z).setColor(color).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packed).setNormal(pose, 0, 0, -1);
        consumer.addVertex(pose, x0, y0, z).setColor(color).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packed).setNormal(pose, 0, 0, -1);
        consumer.addVertex(pose, x0, y1, z).setColor(color).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packed).setNormal(pose, 0, 0, -1);
    }

    public static class Crucible implements BlockEntityRenderer<CrucibleBlockEntity> {
        private final ItemRenderer items;

        public Crucible(BlockEntityRendererProvider.Context context) {
            this.items = context.getItemRenderer();
        }

        @Override
        public void render(CrucibleBlockEntity crucible, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
            FluidStack fluid = crucible.displayFluid();
            poseStack.pushPose();
            poseStack.translate(0.0F, -CrucibleBlock.drop(crucible.getBlockState()), 0.0F);
            float amount = crucible.displayAmount(partialTick);
            float surface = 7.0F + 6.5F * amount / CrucibleBlockEntity.CAPACITY;
            if (!fluid.isEmpty() && amount > 0.5F) {
                surface(poseStack, buffers, fluid, 5.5F, 5.5F, 10.5F, 10.5F, surface, light, 1.0F);
            }
            float melt = crucible.meltFraction();
            int index = 0;
            for (ItemStack stack : crucible.getInputs()) {
                if (stack.isEmpty()) {
                    continue;
                }
                poseStack.pushPose();
                float scale = 0.22F * (1.0F - 0.7F * melt);
                poseStack.translate(0.5, (surface + 0.3F + index * 0.6F * (1.0F - melt) - 1.2F * melt) / 16.0F, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(index * 67.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.scale(scale, scale, scale);
                this.items.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, buffers, crucible.getLevel(), (int) crucible.getBlockPos().asLong() + index);
                poseStack.popPose();
                index++;
            }
            poseStack.popPose();
        }

        @Override
        public AABB getRenderBoundingBox(CrucibleBlockEntity crucible) {
            BlockPos pos = crucible.getBlockPos();
            return new AABB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        }
    }

    private static void surface(PoseStack poseStack, MultiBufferSource buffers, FluidStack fluid, float minX, float minZ, float maxX, float maxZ, float height, int light, float shade) {
        surface(poseStack, buffers, fluid, minX, minZ, maxX, maxZ, height, light, shade, 255);
    }

    private static void surface(PoseStack poseStack, MultiBufferSource buffers, FluidStack fluid, float minX, float minZ, float maxX, float maxZ, float height, int light, float shade, int alpha) {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(fluid));
        int tint = extensions.getTintColor(fluid);
        int color = (alpha << 24) | ((int) (((tint >> 16) & 0xFF) * shade) << 16) | ((int) (((tint >> 8) & 0xFF) * shade) << 8) | (int) ((tint & 0xFF) * shade);
        int glow = Math.round(fluid.getFluid().getFluidType().getLightLevel(fluid) * shade * alpha / 255.0F);
        int packed = LightTexture.pack(Math.max(LightTexture.block(light), glow), LightTexture.sky(light));
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();
        float x0 = minX / 16.0F;
        float x1 = maxX / 16.0F;
        float z0 = minZ / 16.0F;
        float z1 = maxZ / 16.0F;
        float y = height / 16.0F;
        float u0 = sprite.getU(minX / 16.0F);
        float u1 = sprite.getU(maxX / 16.0F);
        float v0 = sprite.getV(minZ / 16.0F);
        float v1 = sprite.getV(maxZ / 16.0F);
        vertex(consumer, pose, x0, y, z0, u0, v0, color, packed);
        vertex(consumer, pose, x0, y, z1, u0, v1, color, packed);
        vertex(consumer, pose, x1, y, z1, u1, v1, color, packed);
        vertex(consumer, pose, x1, y, z0, u1, v0, color, packed);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int color, int light) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0, 1, 0);
    }
}
