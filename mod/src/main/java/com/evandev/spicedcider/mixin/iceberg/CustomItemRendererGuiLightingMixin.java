package com.evandev.spicedcider.mixin.iceberg;

import com.anthonyhilyard.iceberg.renderer.CustomItemRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("iceberg")
@Mixin(value = CustomItemRenderer.class, remap = false)
public class CustomItemRendererGuiLightingMixin {

    @WrapOperation(method = "renderItemModelIntoGUIWithAlpha", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;scale(FFF)Lorg/joml/Matrix4f;"))
    private Matrix4f spicedcider$flipViewY(Matrix4fStack stack, float x, float y, float z, Operation<Matrix4f> original) {
        return original.call(stack, x, -y, z);
    }

    @WrapOperation(method = "renderItemModelIntoGUIWithAlpha", at = @At(value = "INVOKE", target = "Lcom/anthonyhilyard/iceberg/renderer/CustomItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
    private void spicedcider$flipPoseY(CustomItemRenderer renderer, ItemStack stack, ItemDisplayContext context, boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, BakedModel model, Operation<Void> original) {
        poseStack.scale(1.0F, -1.0F, 1.0F);
        original.call(renderer, stack, context, leftHanded, poseStack, buffers, light, overlay, model);
    }
}
