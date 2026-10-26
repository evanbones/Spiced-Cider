package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.lodgen.LodThreads;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldGenRegion.class)
public class WorldGenRegionLodMixin {

    @WrapMethod(method = "isOldChunkAround")
    private boolean spicedcider$noBlendingForLod(ChunkPos pos, int radius, Operation<Boolean> original) {
        return !LodThreads.isLodThread() && original.call(pos, radius);
    }

    @WrapOperation(method = "setBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;onBlockStateChange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void spicedcider$noPoiForLod(ServerLevel level, BlockPos pos, BlockState previous, BlockState state, Operation<Void> original) {
        if (!LodThreads.isLodThread()) original.call(level, pos, previous, state);
    }
}
