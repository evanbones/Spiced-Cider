package com.evandev.spicedcider.mixin.beams.client;

import com.evandev.spicedcider.compat.beams.BeamCollisionIndex;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.syl.beams.client.renderer.beam.BeamGhostRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("beams")
@Mixin(value = BeamGhostRenderer.class, remap = false)
public class BeamGhostRendererMixin {

    @WrapOperation(method = "onRenderLevelStage", at = @At(value = "INVOKE", target = "Lcom/syl/beams/client/renderer/beam/BeamPrismRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;IIII)V"))
    private static void spicedcider$tintObstructed(PoseStack poseStack, VertexConsumer consumer, Vec3 start, Vec3 end, Vec3 offset, BlockAndTintGetter level, TextureAtlasSprite sprite, int r, int g, int b, int a, Operation<Void> original) {
        if (level instanceof Level world && BeamCollisionIndex.obstructedBetween(world, start, end)) {
            original.call(poseStack, consumer, start, end, offset, level, sprite, 255, 70, 70, a);
            return;
        }
        original.call(poseStack, consumer, start, end, offset, level, sprite, r, g, b, a);
    }
}
