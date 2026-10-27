package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.locate.StructurePrefetch;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorLocateMixin {

    @Inject(method = "getNearestGeneratedStructure(Ljava/util/Set;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/StructureManager;IIIZJLnet/minecraft/world/level/levelgen/structure/placement/RandomSpreadStructurePlacement;)Lcom/mojang/datafixers/util/Pair;",
            at = @At("HEAD"))
    private static void spicedcider$prefetchRing(Set<Holder<Structure>> structures, LevelReader level, StructureManager structureManager, int chunkX, int chunkZ, int ring,
                                                 boolean skipKnownStructures, long seed, RandomSpreadStructurePlacement placement,
                                                 CallbackInfoReturnable<Pair<BlockPos, Holder<Structure>>> cir) {
        StructurePrefetch.warmRing(structureManager, structures, chunkX, chunkZ, ring, seed, placement);
    }
}
