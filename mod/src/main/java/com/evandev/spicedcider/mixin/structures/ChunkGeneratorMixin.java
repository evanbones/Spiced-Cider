package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin({ChunkGenerator.class})
public class ChunkGeneratorMixin {
    @ModifyVariable(method = {"getNearestGeneratedStructure(Ljava/util/Set;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/StructureManager;IIIZJLnet/minecraft/world/level/levelgen/structure/placement/RandomSpreadStructurePlacement;)Lcom/mojang/datafixers/util/Pair;"}, argsOnly = true, ordinal = 2, at = @At("HEAD"))
    private static int setRadius2(int org) {
        return Math.min(SpicedCiderConfig.COMMON.globalSearchRadius.get(), org);
    }

    @ModifyVariable(method = {"findNearestMapStructure"}, argsOnly = true, ordinal = 0, at = @At("HEAD"))
    private int setRadius(int org) {
        return Math.min(SpicedCiderConfig.COMMON.globalSearchRadius.get(), org);
    }
}