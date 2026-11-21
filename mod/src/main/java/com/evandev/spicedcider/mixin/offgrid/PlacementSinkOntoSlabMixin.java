package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.offgrid.OffgridBeds;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Placement;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(value = Placement.class, remap = false)
public class PlacementSinkOntoSlabMixin {
    @Unique
    private static final TagKey<Block> spicedcider$SINKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "offgrid_sinks_onto_surfaces"));

    @WrapOperation(method = "plan", at = @At(value = "INVOKE", target = "Ldev/tazer/offgrid/world/Placement;crossesInto(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/Direction;)Z"))
    private static boolean spicedcider$sinkOntoSurface(BlockState state, Vec3 offset, Direction face, Operation<Boolean> original,
                                                       @Local(argsOnly = true) Level level, @Local(argsOnly = true, ordinal = 0) BlockPos target) {
        if (face == Direction.UP && offset.y < 0.0 && offset.y > -1.0 && state.is(spicedcider$SINKS) && OffgridBeds.otherHalfSinks(level, target, state)) {
            return false;
        }
        return original.call(state, offset, face);
    }
}
