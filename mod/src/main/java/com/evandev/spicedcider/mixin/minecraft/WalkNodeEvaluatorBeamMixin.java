package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.compat.beams.BeamCollisionIndex;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WalkNodeEvaluator.class)
public class WalkNodeEvaluatorBeamMixin {

    @ModifyReturnValue(method = "getPathTypeOfMob", at = @At("RETURN"))
    private PathType spicedcider$avoidBeams(PathType original, PathfindingContext context, int x, int y, int z, Mob mob) {
        if (mob.getPathfindingMalus(original) < 0.0F) return original;
        int width = Mth.floor(mob.getBbWidth() + 1.0F);
        int height = Mth.floor(mob.getBbHeight() + 1.0F);
        return BeamCollisionIndex.blocksCell(mob.level(), x, y, z, width, height) ? PathType.FENCE : original;
    }
}
