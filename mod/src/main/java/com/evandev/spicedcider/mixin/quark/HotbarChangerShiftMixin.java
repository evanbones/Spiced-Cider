package com.evandev.spicedcider.mixin.quark;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.management.module.HotbarChangerModule;

@IfModLoaded("quark")
@Mixin(HotbarChangerModule.Client.class)
public class HotbarChangerShiftMixin {

    @WrapOperation(
            method = {"hudHeathPre", "hudOverlay"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
            ),
            remap = false
    )
    private void spicedcider$leaveShiftToLift(PoseStack pose, float x, float y, float z, Operation<Void> original) {
    }
}
