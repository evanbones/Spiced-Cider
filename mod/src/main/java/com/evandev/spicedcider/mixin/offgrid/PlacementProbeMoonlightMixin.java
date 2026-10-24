package com.evandev.spicedcider.mixin.offgrid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Placement;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacement;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacementsAPI;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@IfModLoaded("offgrid")
@IfModLoaded("moonlight")
@Mixin(value = Placement.class, remap = false)
public class PlacementProbeMoonlightMixin {

    @WrapOperation(method = "lambda$decide$2", at = @At(value = "INVOKE", target = "Ldev/tazer/offgrid/mixin/access/BlockItemAccessor;offgrid$getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState spicedcider$probeMoonlightPlacement(@Coerce Object item, BlockPlaceContext context, Operation<BlockState> original) {
        BlockState state = original.call(item, context);
        if (state != null) {
            return state;
        }
        AdditionalItemPlacement placement = AdditionalItemPlacementsAPI.getBehavior((Item) item);
        return placement == null ? null : placement.overrideGetPlacementState(context);
    }
}
