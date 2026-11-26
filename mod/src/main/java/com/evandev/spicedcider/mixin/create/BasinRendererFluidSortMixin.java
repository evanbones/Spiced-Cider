package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.client.render.LateTranslucentFluidBuffer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.processing.basin.BasinRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import com.evandev.spicedcider.config.SpicedCiderConfig;

@IfModLoaded("create")
@Mixin(BasinRenderer.class)
public class BasinRendererFluidSortMixin {

    @ModifyConstant(method = "renderFluids", constant = @Constant(floatValue = 2000.0F))
    private float spicedcider$fullAtOneBucket(float capacity) {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.fullerBasinFluids, false) ? 1000.0F : capacity;
    }

    @ModifyArg(method = "renderFluids", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/render/FluidRenderHelper;renderFluidBox(Ljava/lang/Object;FFFFFFLnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IZZ)V"), index = 7)
    private MultiBufferSource spicedcider$drawAfterItems(MultiBufferSource buffer) {
        return new LateTranslucentFluidBuffer(buffer);
    }
}
