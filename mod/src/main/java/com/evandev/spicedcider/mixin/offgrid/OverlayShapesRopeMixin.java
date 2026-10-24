package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.OverlayShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(value = OverlayShapes.class, remap = false)
public class OverlayShapesRopeMixin {
    @Unique
    private static final double spicedcider$ROPE_MIN = 7.5D / 16.0D;
    @Unique
    private static final double spicedcider$ROPE_MAX = 8.5D / 16.0D;

    @ModifyReturnValue(method = {
            "of(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            "of(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;Ldev/tazer/offgrid/world/Overlay;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
    }, at = @At("RETURN"))
    private static VoxelShape spicedcider$ropeToCeiling(VoxelShape shape, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Vec3 offset) {
        if (shape.isEmpty() || !state.is(ModTags.Blocks.HANGS_FROM_CEILING)) {
            return shape;
        }
        double top = shape.max(Direction.Axis.Y) - offset.y;
        if (top >= 1.0D) {
            return shape;
        }
        return Shapes.or(shape, Shapes.box(spicedcider$ROPE_MIN, top, spicedcider$ROPE_MIN, spicedcider$ROPE_MAX, 1.0D, spicedcider$ROPE_MAX).move(offset.x, offset.y, offset.z));
    }
}
