package com.evandev.spicedcider.mixin.bigwater;

import com.evandev.spicedcider.compat.bigwater.BigWaterLodTextures;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("bigwater")
@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererVoxyMixin {

    @ModifyExpressionValue(method = "tesselate", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/textures/FluidSpriteCache;getFluidSprites(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/FluidState;)[Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private TextureAtlasSprite[] spicedcider$useBigWaterSpritesForVoxy(TextureAtlasSprite[] original, @Local(argsOnly = true) BlockAndTintGetter level, @Local(argsOnly = true) FluidState fluid) {
        return BigWaterLodTextures.begin(level, fluid, original);
    }

    @Inject(method = "tesselate", at = @At("RETURN"))
    private void spicedcider$endBigWaterRemap(CallbackInfo ci) {
        BigWaterLodTextures.end();
    }

    @WrapMethod(method = "vertex(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFFI)V")
    private void spicedcider$scaleBigWaterUv(VertexConsumer consumer, float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int packedLight, Operation<Void> original) {
        TextureAtlasSprite sprite = BigWaterLodTextures.scaledSpriteAt(u, v);
        if (sprite != null) {
            float scale = BigWaterLodTextures.scale();
            u = sprite.getU0() + (u - sprite.getU0()) * scale;
            v = sprite.getV0() + (v - sprite.getV0()) * scale;
        }
        original.call(consumer, x, y, z, red, green, blue, alpha, u, v, packedLight);
    }
}
