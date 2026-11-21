package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.stepup.SmoothStep;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public abstract class CameraSmoothStepMixin {

    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private void spicedcider$smoothStep(Camera camera, double x, double y, double z, Operation<Void> original,
                                        BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (camera != mc.gameRenderer.getMainCamera() || entity != mc.player) {
            original.call(camera, x, y, z);
            return;
        }
        double feet = Mth.lerp(partialTick, entity.yo, entity.getY());
        double smoothed = SmoothStep.adjust(feet, entity.getRootVehicle().onGround());
        original.call(camera, x, y + smoothed - feet, z);
    }
}
