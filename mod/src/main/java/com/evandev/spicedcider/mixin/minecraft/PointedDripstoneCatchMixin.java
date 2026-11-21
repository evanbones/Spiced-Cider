package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.dripping.DripCatching;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneCatchMixin {

    @WrapOperation(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;findFillableCauldronBelowStalactiteTip(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;)Lnet/minecraft/core/BlockPos;"))
    private static BlockPos spicedcider$catchInContainers(Level level, BlockPos tip, Fluid fluid, Operation<BlockPos> original) {
        BlockPos cauldron = original.call(level, tip, fluid);
        if (cauldron == null && level instanceof ServerLevel server) {
            DripCatching.dripstone(server, tip, fluid);
        }
        return cauldron;
    }
}
