package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.interfaces.IWebShooter;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpiderModel.class)
public class SpiderModelMixin {

    @Shadow
    @Final
    private ModelPart root;
    @Unique
    private float cider$originalBodyRotateAngleX;
    @Unique
    private float cider$originalBodyRotationPointY;
    @Unique
    private float cider$originalBodyRotationPointZ;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(CallbackInfo callbackInfo) {
        this.cider$originalBodyRotateAngleX = cider$getBody1().xRot;
        this.cider$originalBodyRotationPointY = cider$getBody1().y;
        this.cider$originalBodyRotationPointZ = cider$getBody1().z;
    }

    @Unique
    private ModelPart cider$getBody1() {
        return root.getChild("body1");
    }

    @Inject(at = @At("RETURN"), method = "setupAnim")
    private void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {
        if (entityIn instanceof Mob && entityIn instanceof IWebShooter webShooter) {
            if (webShooter.cider$isWebShooting()) {
                cider$getBody1().xRot = ((float) Math.PI / 6F);
                cider$getBody1().y = cider$originalBodyRotationPointY - 5;
                cider$getBody1().z = cider$originalBodyRotationPointZ - 2;
            } else {
                cider$getBody1().xRot = this.cider$originalBodyRotateAngleX;
                cider$getBody1().y = cider$originalBodyRotationPointY;
                cider$getBody1().z = cider$originalBodyRotationPointZ;
            }
        }
    }
}