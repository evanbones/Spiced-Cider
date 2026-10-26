package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.compat.offgrid.OffgridOverlayStates;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(LeashFenceKnotEntity.class)
public class LeashKnotOverlayFenceMixin {
    @WrapOperation(method = "survives", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState spicedcider$overlayFence(Level level, BlockPos pos, Operation<BlockState> original) {
        return OffgridOverlayStates.matching(level, pos, original.call(level, pos), BlockTags.FENCES);
    }
}
