package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.interfaces.IEndCrystalHealer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalRenderer.class)
public class EndCrystalRendererMixin {

    @Inject(method = "render*", at = @At("TAIL"))
    private void spicedcider$renderSmoothBeam(EndCrystal crystal, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (crystal instanceof IEndCrystalHealer healer) {
            LivingEntity target = healer.spicedcider$getHealingTarget();

            if (target != null) {
                float crystalX = (float) Mth.lerp(partialTicks, crystal.xo, crystal.getX());
                float crystalY = (float) Mth.lerp(partialTicks, crystal.yo, crystal.getY());
                float crystalZ = (float) Mth.lerp(partialTicks, crystal.zo, crystal.getZ());

                float targetX = (float) Mth.lerp(partialTicks, target.xo, target.getX());
                float targetY = (float) Mth.lerp(partialTicks, target.yo, target.getY()) + (target.getBbHeight() / 2.0F);
                float targetZ = (float) Mth.lerp(partialTicks, target.zo, target.getZ());

                float dx = targetX - crystalX;
                float dy = targetY - crystalY;
                float dz = targetZ - crystalZ;

                EnderDragonRenderer.renderCrystalBeams(dx, dy, dz, partialTicks, crystal.tickCount, poseStack, bufferSource, packedLight);
            }
        }
    }
}