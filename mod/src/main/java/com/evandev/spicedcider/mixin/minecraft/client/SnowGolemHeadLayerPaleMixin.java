package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.content.entity.PaleSnowGolem;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SnowGolemHeadLayer.class)
public abstract class SnowGolemHeadLayerPaleMixin {
    @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/SnowGolem;FFFFFF)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;CARVED_PUMPKIN:Lnet/minecraft/world/level/block/Block;"))
    private Block spicedcider$paleHead(Block original, PoseStack poseStack, MultiBufferSource buffer, int light, SnowGolem golem) {
        return ((PaleSnowGolem) golem).spicedcider$hasPalePumpkin() ? ModPaleGardenBlocks.CARVED_PALE_PUMPKIN.get() : original;
    }
}
