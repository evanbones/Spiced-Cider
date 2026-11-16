package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.client.api.modules.waypoints.LocatorBarRenderer;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("vanillabackport")
@Mixin(LocatorBarRenderer.class)
public class LocatorBarDepthMixin {

    @Unique
    private float spicedcider$backgroundY;

    @Unique
    private float spicedcider$backgroundZ;

    @Inject(method = "renderBackground", at = @At("HEAD"))
    private void spicedcider$captureBackgroundTransform(GuiGraphics guiGraphics, CallbackInfo ci) {
        Matrix4f pose = guiGraphics.pose().last().pose();
        this.spicedcider$backgroundY = pose.m31();
        this.spicedcider$backgroundZ = pose.m32();
    }

    @WrapMethod(method = "renderWaypoints")
    private void spicedcider$alignWithBackground(GuiGraphics guiGraphics, Operation<Void> original) {
        Matrix4f pose = guiGraphics.pose().last().pose();
        float shift = this.spicedcider$backgroundY - pose.m31();
        float lift = Math.max(0.0F, this.spicedcider$backgroundZ + 1.0F - pose.m32());
        if (shift == 0.0F && lift == 0.0F) {
            original.call(guiGraphics);
            return;
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, shift, lift);
        original.call(guiGraphics);
        guiGraphics.pose().popPose();
    }
}
