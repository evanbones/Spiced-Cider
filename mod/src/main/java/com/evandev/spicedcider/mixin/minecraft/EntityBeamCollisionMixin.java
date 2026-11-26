package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.compat.beams.BeamCollisionIndex;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public class EntityBeamCollisionMixin {

    @ModifyReturnValue(method = "collectColliders", at = @At("RETURN"))
    private static List<VoxelShape> spicedcider$addBeamColliders(List<VoxelShape> original, @Local(argsOnly = true) Level level, @Local(argsOnly = true) AABB boundingBox) {
        List<VoxelShape> beams = BeamCollisionIndex.collect(level, boundingBox);
        if (beams == null) return original;
        List<VoxelShape> combined = new ArrayList<>(beams.size() + original.size());
        combined.addAll(beams);
        combined.addAll(original);
        return combined;
    }
}
