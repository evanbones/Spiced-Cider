package com.evandev.spicedcider.mixin.offgrid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Overlays;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(value = Overlays.class, remap = false)
public class OverlaysEdgeContactMixin {
    @Unique
    private static final double spicedcider$SLIVER = 1.0D / 16.0D - 1.0E-4D;

    @WrapOperation(method = "collidesInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"))
    private static boolean spicedcider$ignoreEdgeSlivers(VoxelShape first, VoxelShape second, BooleanOp op, Operation<Boolean> original) {
        if (!original.call(first, second, op)) return false;
        AABB overlap = Shapes.join(first, second, BooleanOp.AND).bounds();
        int thin = 0;
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (overlap.max(axis) - overlap.min(axis) < spicedcider$SLIVER) thin++;
        }
        return thin < 2;
    }
}
