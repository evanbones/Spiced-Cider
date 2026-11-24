package com.evandev.spicedcider.compat.create;

import com.evandev.spicedcider.client.renderer.CoolingItemBuffer;
import com.evandev.spicedcider.content.casting.BasinCastState;
import com.evandev.spicedcider.content.casting.BasinCastingRecipe;
import com.evandev.spicedcider.registry.ModAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class BasinCastRenderer {
    private static final float FLOOR = 2.0F;
    private static final float INNER = 12.0F;
    private static final float WALL = 2.0F;

    public static boolean isCasting(BasinBlockEntity basin) {
        return !BasinCastSync.finished(basin).isEmpty() || !basin.getData(ModAttachments.BASIN_CAST).isEmpty();
    }

    public static void render(BasinBlockEntity basin, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        Level level = basin.getLevel();
        if (level == null) return;
        ItemStack finished = BasinCastSync.finished(basin);
        if (!finished.isEmpty()) {
            block(poseStack, buffers, finished, light, 255, 0, basin);
            return;
        }
        BasinCastState state = basin.getData(ModAttachments.BASIN_CAST);
        if (state.isEmpty()) return;
        RecipeHolder<BasinCastingRecipe> recipe = BasinCasting.find(level, state.fluid());
        if (recipe == null) return;
        float fill = Math.min(1.0F, state.fluid().getAmount() / (float) recipe.value().amount());
        float cooling = fill < 1.0F ? 0.0F : Math.min(1.0F, BasinCastSync.coolingTicks(basin, state, partialTick) / recipe.value().time());
        int blockAlpha = Math.round(255.0F * cooling);
        int fluidAlpha = cooling > 0.25F ? Math.round(255.0F * (1.0F - (cooling - 0.25F) / 0.75F)) : 255;
        if (blockAlpha > 0) block(poseStack, buffers, recipe.value().result(), light, blockAlpha, fluidAlpha, basin);
        if (fluidAlpha > 0) surface(poseStack, buffers, state.fluid(), FLOOR + INNER * fill, light, fluidAlpha);
    }

    private static void block(PoseStack poseStack, MultiBufferSource buffers, ItemStack stack, int light, int alpha, int heat, BasinBlockEntity basin) {
        poseStack.pushPose();
        poseStack.translate(0.5F, (FLOOR + INNER / 2.0F) / 16.0F, 0.5F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        MultiBufferSource target = alpha < 255 || heat > 0 ? new CoolingItemBuffer(buffers, alpha, heat) : buffers;
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY,
                poseStack, target, basin.getLevel(), (int) basin.getBlockPos().asLong());
        poseStack.popPose();
    }

    private static void surface(PoseStack poseStack, MultiBufferSource buffers, FluidStack fluid, float height, int light, int alpha) {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture(fluid));
        int color = (alpha << 24) | (extensions.getTintColor(fluid) & 0xFFFFFF);
        int glow = Math.round(fluid.getFluid().getFluidType().getLightLevel(fluid) * alpha / 255.0F);
        int packed = LightTexture.pack(Math.max(LightTexture.block(light), glow), LightTexture.sky(light));
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS));
        PoseStack.Pose pose = poseStack.last();
        float min = WALL / 16.0F;
        float max = (16.0F - WALL) / 16.0F;
        float y = height / 16.0F;
        float u0 = sprite.getU(min);
        float u1 = sprite.getU(max);
        float v0 = sprite.getV(min);
        float v1 = sprite.getV(max);
        vertex(consumer, pose, min, y, min, u0, v0, color, packed);
        vertex(consumer, pose, min, y, max, u0, v1, color, packed);
        vertex(consumer, pose, max, y, max, u1, v1, color, packed);
        vertex(consumer, pose, max, y, min, u1, v0, color, packed);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int color, int light) {
        consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }
}
