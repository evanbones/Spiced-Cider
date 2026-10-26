package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.renderer.LeashStyle;
import com.evandev.spicedcider.compat.vanillabackport.VanillaBackportCompat;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererLeashMixin {
    private static final String ADD_VERTEX_PAIR = "Lnet/minecraft/client/renderer/entity/EntityRenderer;addVertexPair(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Matrix4f;FFFIIIIFFFFIZ)V";

    @Inject(method = "renderLeash", at = @At("HEAD"), cancellable = true)
    private void spicedcider$leaveToVanillaBackport(CallbackInfo ci) {
        if (VanillaBackportCompat.modernLeashes()) ci.cancel();
    }

    @ModifyExpressionValue(method = "renderLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;invSqrt(F)F"))
    private float spicedcider$width(float scale) {
        return scale * LeashStyle.SCALE;
    }

    @ModifyArg(method = "renderLeash", at = @At(value = "INVOKE", target = ADD_VERTEX_PAIR), index = 9)
    private float spicedcider$thickness(float yOffset) {
        return LeashStyle.THICKNESS;
    }

    @ModifyArg(method = "renderLeash", at = @At(value = "INVOKE", target = ADD_VERTEX_PAIR), index = 10)
    private float spicedcider$faceOffset(float dy) {
        return dy > 0.0F ? LeashStyle.THICKNESS : 0.0F;
    }

    @WrapOperation(method = "addVertexPair", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static VertexConsumer spicedcider$color(VertexConsumer consumer, float red, float green, float blue, float alpha, Operation<VertexConsumer> original) {
        return LeashStyle.color(consumer, red, alpha, original);
    }
}
