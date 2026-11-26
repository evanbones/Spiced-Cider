package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.NMLConfig;
import com.farcr.nomansland.common.block.torches.SconceTorchBlock;
import com.farcr.nomansland.common.block.torches.SconceWallTorchBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = {SconceTorchBlock.class, SconceWallTorchBlock.class}, remap = false)
public class SconceTorchExplosionMixin {

    @WrapOperation(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean spicedcider$noExplosionExtinguish(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        return NMLConfig.TORCH_EXTINGUISHING.get() && original.call(level, pos, state);
    }
}
