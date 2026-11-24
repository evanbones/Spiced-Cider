package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.stepup.SmoothStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererSmoothStepMixin {

    @WrapMethod(method = "renderEntity")
    private void spicedcider$glideMount(Entity entity, double camX, double camY, double camZ, float partialTick,
                                        PoseStack poseStack, MultiBufferSource bufferSource, Operation<Void> original) {
        double offset = SmoothStep.offset();
        LocalPlayer player = Minecraft.getInstance().player;
        if (offset != 0.0 && player != null && player.isPassenger() && entity.getRootVehicle() == player.getRootVehicle()) {
            camY -= offset;
        }
        original.call(entity, camX, camY, camZ, partialTick, poseStack, bufferSource);
    }
}
