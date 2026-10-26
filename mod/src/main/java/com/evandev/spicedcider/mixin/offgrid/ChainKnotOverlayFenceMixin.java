package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.compat.offgrid.OffgridOverlayStates;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@IfModLoaded("connectiblechains")
@Mixin(targets = "com.evandev.connectiblechains.entity.ChainKnotEntity", remap = false)
public class ChainKnotOverlayFenceMixin {
    @Unique
    private static final TagKey<Block> spicedcider$CHAIN_CONNECTIBLE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("connectiblechains", "chain_connectible"));

    @WrapOperation(method = {"survives", "getKnotScale", "computeKnotScale"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState spicedcider$overlayFence(Level level, BlockPos pos, Operation<BlockState> original) {
        return OffgridOverlayStates.matching(level, pos, original.call(level, pos), spicedcider$CHAIN_CONNECTIBLE);
    }
}
