package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.content.casting.CrucibleBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Placement;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(value = Placement.class, remap = false)
public class PlacementCrucibleSeatMixin {
    @WrapOperation(method = "decide", at = @At(value = "INVOKE", target = "Ldev/tazer/offgrid/world/Overlays;canOffset(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static boolean spicedcider$seatCrucibleOnGrid(BlockState state, Operation<Boolean> original, @Local(argsOnly = true) BlockPlaceContext context) {
        if (state != null && state.getBlock() instanceof CrucibleBlock && CrucibleBlock.isSeat(context.getLevel().getBlockState(context.getClickedPos().below()))) {
            return false;
        }
        return original.call(state);
    }
}
