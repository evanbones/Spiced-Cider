package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.compat.offgrid.OverlaySnowRender;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tazer.offgrid.client.OverlayGeometry;
import dev.tazer.offgrid.neoforge.client.NeoForgeOverlayGeometry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@IfModLoaded("snowrealmagic")
@Mixin(value = NeoForgeOverlayGeometry.class, remap = false)
public class OverlayGeometrySnowMixin {
    @Unique
    private static final ThreadLocal<OverlayGeometry.Entry> spicedcider$lastSnowy = new ThreadLocal<>();

    @WrapOperation(method = "lambda$render$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"))
    private static void spicedcider$renderSnowCovered(ModelBlockRenderer renderer, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, RandomSource random, long seed, int packedOverlay, ModelData data, RenderType renderType, Operation<Void> original, @Local OverlayGeometry.Entry entry, @Local(argsOnly = true) AddSectionGeometryEvent.SectionRenderingContext context) {
        BlockEntity blockEntity = entry.overlay().getBlockEntity();
        if (!OverlaySnowRender.isSnowCovered(blockEntity)) {
            original.call(renderer, level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay, data, renderType);
            return;
        }
        if (spicedcider$lastSnowy.get() == entry) return;
        spicedcider$lastSnowy.set(entry);
        OverlaySnowRender.render(blockEntity, entry.open(), context, state, pos, poseStack, checkSides, random, seed, packedOverlay);
    }
}
