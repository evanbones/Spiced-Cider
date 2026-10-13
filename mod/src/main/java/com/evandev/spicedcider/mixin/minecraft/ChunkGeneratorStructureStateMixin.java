package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkGeneratorStructureState.class)
public class ChunkGeneratorStructureStateMixin {

    @WrapMethod(method = "hasBiomesForStructureSet")
    private static boolean spicedcider$skipDisabledSets(StructureSet structureSet, BiomeSource biomeSource, Operation<Boolean> original) {
        if (structureSet.structures().stream().allMatch(entry -> entry.structure().is(ModTags.Structures.DISABLED))) return false;
        return original.call(structureSet, biomeSource);
    }
}
