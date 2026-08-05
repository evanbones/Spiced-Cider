package com.evandev.spicedcider.mixin.blockgrid;

import com.evandev.spicedcider.blockgrid.SupportOffsets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderOffsetMixin {
    @Inject(method = "setupAndRender", at = @At("HEAD"))
    private static void spicedcider$beginOffsetPose(BlockEntityRenderer<?> renderer, BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        poseStack.pushPose();
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        Vec3 offset = SupportOffsets.offsetFor(blockEntity.getBlockState(), level, blockEntity.getBlockPos());
        if (offset != Vec3.ZERO) {
            poseStack.translate(offset.x, offset.y, offset.z);
        }
    }

    @Inject(method = "setupAndRender", at = @At("RETURN"))
    private static void spicedcider$endOffsetPose(BlockEntityRenderer<?> renderer, BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        poseStack.popPose();
    }
}
