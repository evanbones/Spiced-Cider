package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.api.modules.leash_behavior.LeashFeatureRenderer;
import com.blackgear.vanillabackport.common.api.modules.leash_behavior.LeashState;
import com.evandev.spicedcider.client.renderer.LeashStyle;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModLoaded("vanillabackport")
@Mixin(LeashFeatureRenderer.class)
public class LeashFeatureRendererMixin {

    @ModifyArg(method = "renderLeash", at = @At(value = "INVOKE", target = "Lcom/blackgear/vanillabackport/common/api/modules/leash_behavior/LeashFeatureRenderer;addVertexPair(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Matrix4f;FFFFFFIZLcom/blackgear/vanillabackport/common/api/modules/leash_behavior/LeashState;)V"), index = 5)
    private float spicedcider$crossedFaces(VertexConsumer builder, Matrix4f pose, float dx, float dy, float dz, float fudge, float dxOff, float dzOff, int segment, boolean backwards, LeashState state) {
        return backwards ? 0.0F : LeashStyle.THICKNESS;
    }

    @ModifyExpressionValue(method = "renderLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;invSqrt(F)F"))
    private float spicedcider$width(float scale) {
        return scale * LeashStyle.SCALE / 2.0F;
    }

    @ModifyExpressionValue(method = "addVertexPair", at = @At(value = "CONSTANT", args = "floatValue=0.05"))
    private static float spicedcider$thickness(float thickness) {
        return LeashStyle.THICKNESS;
    }

    @WrapOperation(method = "addVertexPair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static VertexConsumer spicedcider$color(VertexConsumer consumer, float red, float green, float blue, float alpha, Operation<VertexConsumer> original) {
        return LeashStyle.color(consumer, red, alpha, original);
    }
}
