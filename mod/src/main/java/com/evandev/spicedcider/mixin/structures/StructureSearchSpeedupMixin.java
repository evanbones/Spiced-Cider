package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.structures.IGeneratorNearbyStructureHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin({ChunkGenerator.class})
public class StructureSearchSpeedupMixin {
    @Inject(method = {"getStructureGeneratingAt"}, at = {@At("HEAD")}, cancellable = true)
    private static void onFind(Set<Holder<Structure>> holderSet, LevelReader level, StructureManager structureManager, boolean load, StructurePlacement placement, ChunkPos pos, CallbackInfoReturnable<Pair<BlockPos, Holder<Structure>>> cir) {
        if (holderSet.isEmpty() || !SpicedCiderConfig.COMMON.useFastStructureLookup.get()) {
            return;
        }


        boolean found = false;

        int min = level.getMinBuildHeight();
        int max = level.getMaxBuildHeight();
        int startY = 65;
        if (startY > max || startY < min) {
            startY = (min + max) / 2;
        }

        int stepSize = 64;
        int upSteps = (max - startY) / 64;
        int downSteps = (startY - min) / 64;
        int[] yLevels = new int[1 + upSteps + downSteps];

        yLevels[0] = startY;
        int index = 1;
        for (int step = 1; step <= Math.max(upSteps, downSteps); step++) {

            if (step <= upSteps) {
                yLevels[index++] = startY + step * 64;
            }

            if (step <= downSteps) {
                yLevels[index++] = startY - step * 64;
            }
        }

        BlockPos worldPos = pos.getWorldPosition();

        int i;
        label49:
        for (i = 0; i < 4; i++) {

            int xQuart = QuartPos.fromBlock(worldPos.getX() + i * 4);
            int zQuart = QuartPos.fromBlock(worldPos.getZ() + i * 4);

            for (int yBlock : yLevels) {

                int yQuart = QuartPos.fromBlock(yBlock);


                Holder<Biome> holder = ((ServerLevel) level).getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(xQuart, yQuart, zQuart, ((ServerLevel) level).getChunkSource().randomState().sampler());

                for (Holder<Structure> structureHolder : holderSet) {

                    if (structureHolder.value().biomes().contains(holder)) {

                        ChunkGenerator chunkGenerator = ((ServerLevel) level).getChunkSource().getGenerator();
                        if (chunkGenerator instanceof IGeneratorNearbyStructureHolder nearbyStructureHolder) {
                            String name;

                            ResourceLocation regID = level.registryAccess().registry(Registries.STRUCTURE).get().getKey(structureHolder.value());
                            if (regID != null) {
                                name = regID.toString();
                            } else {
                                name = "unknown:" + structureHolder.value();
                            }

                            String existing = nearbyStructureHolder.getNearby(SectionPos.asLong(pos.x, yBlock >> 4, pos.z));
                            if (existing != null && !existing.equals(name)) {
                                continue;
                            }
                        }

                        found = true;
                        break label49;
                    }
                }
            }
        }
        if (!found) {
            cir.setReturnValue(null);
        }
    }
}
