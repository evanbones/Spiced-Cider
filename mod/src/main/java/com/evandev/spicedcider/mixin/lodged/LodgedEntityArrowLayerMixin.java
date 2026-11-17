package com.evandev.spicedcider.mixin.lodged;

import com.evandev.spicedcider.compat.lodged.TamedArrowPullingClient;
import com.jvn.lodged.client.LodgedEntityArrowLayer;
import com.jvn.lodged.world.LodgedArrowVisual;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("lodged")
@Mixin(value = LodgedEntityArrowLayer.class, remap = false)
public class LodgedEntityArrowLayerMixin {

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
    )
    private void spicedcider$recordTamedArrow(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity livingEntity,
                                             float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                                             float netHeadYaw, float headPitch, CallbackInfo ci,
                                             @Local LodgedArrowVisual arrow, @Local Vector3f partLocalDirection) {
        TamedArrowPullingClient.record(livingEntity, arrow, poseStack.last(), partLocalDirection, packedLight);
    }
}
