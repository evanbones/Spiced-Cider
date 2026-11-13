package com.evandev.spicedcider.mixin.snowrealmagic;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("snowrealmagic")
@Mixin(value = SnowLayerBlock.class, priority = 1500)
public class SnowLayerFallOnOverlayMixin {

    @WrapOperation(
            method = "fallOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1)
    )
    private BlockState spicedcider$snowStateForOverlay(Level level, BlockPos pos, Operation<BlockState> original, @Local(argsOnly = true) BlockState state) {
        BlockState inWorld = original.call(level, pos);
        if (inWorld.hasProperty(SnowLayerBlock.LAYERS) || !state.hasProperty(SnowLayerBlock.LAYERS)) return inWorld;
        return state;
    }
}
