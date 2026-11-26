package com.evandev.spicedcider.mixin.beams;

import com.evandev.spicedcider.compat.beams.BeamCollisionIndex;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("lithium")
@Mixin(targets = "net.caffeinemc.mods.lithium.common.entity.LithiumEntityCollisions", remap = false)
public class LithiumBeamCollisionMixin {

    @Inject(method = "appendEntityCollisions", at = @At("RETURN"))
    private static void spicedcider$appendBeamCollisions(List<VoxelShape> entityCollisions, Level world, Entity entity, AABB box, CallbackInfo ci) {
        List<VoxelShape> beams = BeamCollisionIndex.collect(world, box);
        if (beams != null) entityCollisions.addAll(beams);
    }
}
