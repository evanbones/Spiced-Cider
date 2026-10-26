package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorDisabledStructureMixin {
    @Unique
    private static final int SPICEDCIDER$CENTER_DEPTH = 8;
    @Unique
    private static final int SPICEDCIDER$EDGE_DEPTH = 10;
    @Unique
    private static final int SPICEDCIDER$REACH = 40;

    @WrapMethod(method = "tryGenerateStructure")
    private boolean spicedcider$skipDisabled(StructureSet.StructureSelectionEntry entry, StructureManager structureManager, RegistryAccess registryAccess,
                                             RandomState random, StructureTemplateManager templateManager, long seed, ChunkAccess chunk,
                                             ChunkPos chunkPos, SectionPos sectionPos, Operation<Boolean> original) {
        if (entry.structure().is(ModTags.Structures.DISABLED)) return false;
        if (entry.structure().is(ModTags.Structures.AVOIDS_OCEAN) && this.spicedcider$isUnderOcean(chunk, chunkPos, random)) return false;
        return original.call(entry, structureManager, registryAccess, random, templateManager, seed, chunk, chunkPos, sectionPos);
    }

    @Unique
    private boolean spicedcider$isUnderOcean(ChunkAccess chunk, ChunkPos chunkPos, RandomState random) {
        ChunkGenerator generator = (ChunkGenerator) (Object) this;
        int seaLevel = generator.getSeaLevel();
        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();
        if (seaLevel - generator.getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, chunk, random) > SPICEDCIDER$CENTER_DEPTH) return true;
        int deep = 0;
        for (int dx = -SPICEDCIDER$REACH; dx <= SPICEDCIDER$REACH; dx += SPICEDCIDER$REACH) {
            for (int dz = -SPICEDCIDER$REACH; dz <= SPICEDCIDER$REACH; dz += SPICEDCIDER$REACH) {
                if ((dx != 0 || dz != 0) && seaLevel - generator.getBaseHeight(x + dx, z + dz, Heightmap.Types.OCEAN_FLOOR_WG, chunk, random) > SPICEDCIDER$EDGE_DEPTH) deep++;
            }
        }
        return deep >= 3;
    }

    @WrapMethod(method = "findNearestMapStructure")
    private Pair<BlockPos, Holder<Structure>> spicedcider$locateEnabledOnly(ServerLevel level, HolderSet<Structure> structures, BlockPos pos,
                                                                          int searchRadius, boolean skipKnownStructures,
                                                                          Operation<Pair<BlockPos, Holder<Structure>>> original) {
        if (structures.stream().noneMatch(holder -> holder.is(ModTags.Structures.DISABLED))) {
            return original.call(level, structures, pos, searchRadius, skipKnownStructures);
        }
        List<Holder<Structure>> enabled = structures.stream().filter(holder -> !holder.is(ModTags.Structures.DISABLED)).toList();
        if (enabled.isEmpty()) return null;
        return original.call(level, HolderSet.direct(enabled), pos, searchRadius, skipKnownStructures);
    }
}
