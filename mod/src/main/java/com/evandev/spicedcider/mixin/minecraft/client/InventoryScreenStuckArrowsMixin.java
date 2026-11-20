package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.stuckarrows.StuckArrowsClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryScreen.class)
public class InventoryScreenStuckArrowsMixin {

    @WrapOperation(method = "renderEntityInInventoryFollowsAngle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventory(Lnet/minecraft/client/gui/GuiGraphics;FFFLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private static void spicedcider$rotateAndRecord(GuiGraphics guiGraphics, float x, float y, float scale, Vector3f translate,
                                                   Quaternionf pose, Quaternionf cameraOrientation, LivingEntity entity, Operation<Void> original) {
        if (!StuckArrowsClient.beginInventoryModel(entity)) {
            original.call(guiGraphics, x, y, scale, translate, pose, cameraOrientation, entity);
            return;
        }
        float yaw = StuckArrowsClient.modelYaw();
        entity.yBodyRot += yaw;
        entity.setYRot(entity.getYRot() + yaw);
        entity.yHeadRot += yaw;
        entity.yHeadRotO += yaw;
        try {
            original.call(guiGraphics, x, y, scale, translate, pose, cameraOrientation, entity);
        } finally {
            StuckArrowsClient.endInventoryModel(guiGraphics);
        }
    }
}
