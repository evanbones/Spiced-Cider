package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.compat.beams.BeamCollisionIndex;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class BlockItemBeamPlacementMixin {

    @ModifyReturnValue(method = "canPlace", at = @At("RETURN"))
    private boolean spicedcider$rejectBeamOverlap(boolean original, BlockPlaceContext context, BlockState state) {
        if (!original) return false;
        return !BeamCollisionIndex.obstructsPlacement(context.getLevel(), context.getClickedPos(), state.getShape(context.getLevel(), context.getClickedPos()));
    }
}
