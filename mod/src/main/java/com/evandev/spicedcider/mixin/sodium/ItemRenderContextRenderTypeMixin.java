package com.evandev.spicedcider.mixin.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("sodium")
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.frapi.render.ItemRenderContext", remap = false)
public abstract class ItemRenderContextRenderTypeMixin {
    @Unique
    private final RandomSource spicedcider$random = RandomSource.create();
    @Unique
    private BakedModel spicedcider$model;

    @Inject(method = "renderModel", at = @At("HEAD"))
    private void spicedcider$captureModel(ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {
        spicedcider$model = model;
    }

    @WrapOperation(method = "computeOutputInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getChunkRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/RenderType;"))
    private RenderType spicedcider$useModelRenderType(BlockState state, Operation<RenderType> original) {
        RenderType layer = original.call(state);
        if (layer == RenderType.translucent() || spicedcider$model == null) return layer;
        spicedcider$random.setSeed(42L);
        return spicedcider$model.getRenderTypes(state, spicedcider$random, ModelData.EMPTY).contains(RenderType.translucent()) ? RenderType.translucent() : layer;
    }
}
