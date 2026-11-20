package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.stuckarrows.StuckArrowPlacement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckInBodyLayer.class)
public abstract class StuckInBodyLayerMixin {

    @Shadow
    protected abstract void renderStuckItem(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity entity,
                                            float x, float y, float z, float partialTick);

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"), cancellable = true)
    private void spicedcider$placeStuckArrows(PoseStack poseStack, MultiBufferSource buffer, int light, LivingEntity entity,
                                              float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                                              float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!((Object) this instanceof ArrowLayer<?, ?>)) return;
        if (StuckArrowPlacement.render((RenderLayer<?, ?>) (Object) this, poseStack, buffer, light, entity,
                (pose, source, packedLight, x, y, z) -> renderStuckItem(pose, source, packedLight, entity, x, y, z, partialTick))) {
            ci.cancel();
        }
    }
}
