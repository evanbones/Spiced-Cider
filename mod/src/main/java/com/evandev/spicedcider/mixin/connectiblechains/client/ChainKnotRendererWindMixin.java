package com.evandev.spicedcider.mixin.connectiblechains.client;

import com.evandev.connectiblechains.client.render.entity.state.ChainKnotEntityRenderState;
import com.evandev.connectiblechains.util.HangingBlockRenderer;
import com.evandev.spicedcider.compat.connectiblechains.client.ChainWindSway;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded(value = "connectiblechains", minVersion = "2.6.0")
@Mixin(targets = "com.evandev.connectiblechains.client.render.entity.ChainKnotEntityRenderer", remap = false)
public abstract class ChainKnotRendererWindMixin {

    @WrapOperation(method = "renderHangingsAlongChain", at = @At(value = "INVOKE", target = "Lcom/evandev/connectiblechains/util/HangingBlockRenderer;render(Lnet/minecraft/client/Minecraft;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FF)V"))
    private void spicedcider$swayHanging(HangingBlockRenderer renderer, Minecraft mc, ResourceLocation blockId, BlockPos pos, PoseStack poseStack,
                                         MultiBufferSource buffers, float x, float y, Operation<Void> original,
                                         @Local(argsOnly = true) Vector3f chainVec, @Local(argsOnly = true) ChainKnotEntityRenderState.ChainData chainData) {
        float angle = ChainWindSway.angle(chainVec, chainData.startPos, x, y, ChainWindSway.HANGING);
        if (angle == 0.0F) {
            original.call(renderer, mc, blockId, pos, poseStack, buffers, x, y);
            return;
        }
        poseStack.pushPose();
        poseStack.translate(x, y, 0.0F);
        poseStack.mulPose(Axis.XP.rotation(angle));
        poseStack.translate(-x, -y, 0.0F);
        original.call(renderer, mc, blockId, pos, poseStack, buffers, x, y);
        poseStack.popPose();
    }

    @WrapOperation(method = "renderBannersAlongChain", at = @At(value = "INVOKE", target = "Lcom/evandev/connectiblechains/client/render/entity/ChainKnotEntityRenderer;transformToBanner(Lcom/mojang/blaze3d/vertex/PoseStack;FF)V"))
    private void spicedcider$swayBanner(PoseStack poseStack, float x, float y, Operation<Void> original,
                                        @Local(argsOnly = true) Vector3f chainVec, @Local(argsOnly = true) ChainKnotEntityRenderState.ChainData chainData) {
        float angle = ChainWindSway.angle(chainVec, chainData.startPos, x, y, ChainWindSway.BANNER);
        if (angle != 0.0F) {
            poseStack.translate(x, y, 0.0F);
            poseStack.mulPose(Axis.XP.rotation(angle));
            poseStack.translate(-x, -y, 0.0F);
        }
        original.call(poseStack, x, y);
    }

    @WrapOperation(method = "renderBuntingsAlongChain", at = @At(value = "INVOKE", target = "Lcom/evandev/connectiblechains/client/SupplementariesCompat;renderBunting(Lnet/minecraft/world/item/DyeColor;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/core/BlockPos;J)V"))
    private void spicedcider$swayBunting(DyeColor color, PoseStack poseStack, MultiBufferSource buffers, int light, BlockPos pos, long gameTime,
                                         Operation<Void> original, @Local(argsOnly = true) Vector3f chainVec,
                                         @Local(argsOnly = true) ChainKnotEntityRenderState.ChainData chainData,
                                         @Local(ordinal = 0) float x, @Local(ordinal = 1) float y) {
        float angle = ChainWindSway.angle(chainVec, chainData.startPos, x, y, ChainWindSway.BUNTING);
        if (angle != 0.0F) {
            poseStack.translate(-0.25F, 0.19F, 0.0F);
            poseStack.mulPose(Axis.XP.rotation(angle));
            poseStack.translate(0.25F, -0.19F, 0.0F);
        }
        original.call(color, poseStack, buffers, light, pos, gameTime);
    }
}
