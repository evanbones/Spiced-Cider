package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.compat.offgrid.OffgridBeds;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Overlays;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(value = Overlays.class, remap = false)
public class OverlaysDeferredOffsetClearMixin {
    @WrapOperation(method = "onBlockChanged", at = @At(value = "INVOKE", target = "Ldev/tazer/offgrid/world/Overlays;keepsOffset(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Z"))
    private static boolean spicedcider$deferSinkingClear(ServerLevel level, BlockPos pos, BlockState oldState, BlockState newState, Vec3 offset, Operation<Boolean> original) {
        if (original.call(level, pos, oldState, newState, offset)) return true;
        if (Overlays.isRemoval(newState) && oldState.is(OffgridBeds.SINKS)) {
            OffgridBeds.clearLater(level, pos);
            return true;
        }
        return false;
    }
}
