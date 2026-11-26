package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.BasinCastRenderer;
import com.evandev.spicedcider.compat.create.BasinCasting;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("create")
@Mixin(value = BasinRenderer.class, remap = false)
public class BasinRendererCastMixin {
    @WrapOperation(method = "renderSafe(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinRenderer;renderFluids(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)F"))
    private float spicedcider$hideFluidsWhileCasting(BasinRenderer renderer, BasinBlockEntity basin, float partialTick, PoseStack poseStack,
                                                     MultiBufferSource buffers, int light, int overlay, Operation<Float> original) {
        return BasinCastRenderer.isCasting(basin) ? 0.0F : original.call(renderer, basin, partialTick, poseStack, buffers, light, overlay);
    }

    @WrapOperation(method = "renderSafe(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/items/IItemHandlerModifiable;getStackInSlot(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack spicedcider$hideCastBlock(IItemHandlerModifiable handler, int slot, Operation<ItemStack> original, BasinBlockEntity basin) {
        ItemStack stack = original.call(handler, slot);
        return basin.getLevel() != null && BasinCasting.isCastResult(basin.getLevel(), stack) ? ItemStack.EMPTY : stack;
    }

    @Inject(method = "renderSafe(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("HEAD"))
    private void spicedcider$renderCast(BasinBlockEntity basin, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, CallbackInfo ci) {
        BasinCastRenderer.render(basin, partialTick, poseStack, buffers, light);
    }
}
