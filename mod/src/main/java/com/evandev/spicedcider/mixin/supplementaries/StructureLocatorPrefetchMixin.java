package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.locate.StructurePrefetch;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.moonlight.api.util.math.Vec2i;
import net.mehvahdjukaar.supplementaries.common.worldgen.StructureLocator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@IfModLoaded("supplementaries")
@Mixin(value = StructureLocator.class, remap = false)
public class StructureLocatorPrefetchMixin {

    @WrapOperation(method = "findNearestStructures(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/HolderSet;Lnet/minecraft/core/BlockPos;IZIIZ)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/moonlight/api/util/math/CircularGridUtils;iterateInRing(IIII)Ljava/util/Iterator;"))
    private static Iterator<Vec2i> spicedcider$prefetchRing(int originX, int originZ, int radius, int gridSize, Operation<Iterator<Vec2i>> original,
                                                          @Local(argsOnly = true) ServerLevel level, @Local(name = "placementsInGrid") List<?> placements,
                                                          @Local(name = "centerChunkX") int centerChunkX, @Local(name = "centerChunkZ") int centerChunkZ,
                                                          @Local(name = "worldSeed") long seed) {
        Iterator<Vec2i> cells = original.call(originX, originZ, radius, gridSize);
        List<Vec2i> ring = new ArrayList<>();
        cells.forEachRemaining(ring::add);
        List<StructurePrefetch.Candidate> candidates = new ArrayList<>(ring.size() * placements.size());
        for (Vec2i cell : ring) {
            for (Object entry : placements) {
                StructureAndPlacementAccessor info = (StructureAndPlacementAccessor) entry;
                RandomSpreadStructurePlacement placement = info.spicedcider$placement();
                candidates.add(new StructurePrefetch.Candidate(placement.getPotentialStructureChunk(seed, centerChunkX + cell.x(), centerChunkZ + cell.y()),
                        info.spicedcider$structure().value(), placement));
            }
        }
        StructurePrefetch.warm(level.structureManager(), candidates);
        return ring.iterator();
    }
}
