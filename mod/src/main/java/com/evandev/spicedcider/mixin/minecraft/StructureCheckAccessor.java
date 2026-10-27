package com.evandev.spicedcider.mixin.minecraft;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(StructureCheck.class)
public interface StructureCheckAccessor {

    @Accessor("loadedChunks")
    Long2ObjectMap<Object2IntMap<Structure>> spicedcider$loadedChunks();

    @Accessor("featureChecks")
    Map<Structure, Long2BooleanMap> spicedcider$featureChecks();

    @Accessor("seed")
    long spicedcider$seed();

    @Accessor("storageAccess")
    ChunkScanAccess spicedcider$storageAccess();

    @Invoker("canCreateStructure")
    boolean spicedcider$canCreateStructure(ChunkPos chunkPos, Structure structure);
}
