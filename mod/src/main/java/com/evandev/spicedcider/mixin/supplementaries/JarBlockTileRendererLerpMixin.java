package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.compat.supplementaries.JarPouring;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.mehvahdjukaar.supplementaries.client.renderers.tiles.JarBlockTileRenderer;
import net.mehvahdjukaar.supplementaries.common.block.tiles.JarBlockTile;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("supplementaries")
@Mixin(value = JarBlockTileRenderer.class, remap = false)
public class JarBlockTileRendererLerpMixin {
    @Inject(method = "render(Lnet/mehvahdjukaar/supplementaries/common/block/tiles/JarBlockTile;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("HEAD"))
    private void spicedcider$rememberEmpty(JarBlockTile tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (tile.getSoftFluidTank() == null || tile.getSoftFluidTank().isEmpty()) JarPouring.markEmpty(tile);
    }

    @WrapOperation(method = "render(Lnet/mehvahdjukaar/supplementaries/common/block/tiles/JarBlockTile;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/moonlight/api/fluids/SoftFluidTank;getHeight(F)F"))
    private float spicedcider$lerpFluidLevel(SoftFluidTank tank, float max, Operation<Float> original, JarBlockTile tile, float partialTicks,
                                             PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        return JarPouring.shownHeight(tile, original.call(tank, max));
    }
}
