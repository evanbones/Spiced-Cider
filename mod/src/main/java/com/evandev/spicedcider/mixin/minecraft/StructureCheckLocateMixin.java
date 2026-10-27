package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureCheck.class)
public class StructureCheckLocateMixin {
    @Shadow
    @Final
    private Long2ObjectMap<Object2IntMap<Structure>> loadedChunks;

    @Shadow
    @Final
    private long seed;

    @WrapMethod(method = "checkStart")
    private StructureCheckResult spicedcider$rejectByFrequencyFirst(ChunkPos chunkPos, Structure structure, StructurePlacement placement, boolean skipKnownStructures,
                                                                   Operation<StructureCheckResult> original) {
        if (this.loadedChunks.containsKey(chunkPos.toLong()) || placement.applyAdditionalChunkRestrictions(chunkPos.x, chunkPos.z, this.seed)) {
            return original.call(chunkPos, structure, placement, skipKnownStructures);
        }
        return StructureCheckResult.START_NOT_PRESENT;
    }
}
