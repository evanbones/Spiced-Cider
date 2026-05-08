package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.structures.IGeneratorNearbyStructureHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;


@Mixin({Structure.class})
public abstract class StructureStartMinDistMixin {
    @Unique
    private static List<BlockPos> getBoundingBoxCorners(BoundingBox box) {
        return List.of(new BlockPos(box
                .minX(), box.minY(), box.minZ()), new BlockPos(box
                .minX(), box.minY(), box.maxZ()), new BlockPos(box
                .minX(), box.maxY(), box.minZ()), new BlockPos(box
                .minX(), box.maxY(), box.maxZ()), new BlockPos(box
                .maxX(), box.minY(), box.minZ()), new BlockPos(box
                .maxX(), box.minY(), box.maxZ()), new BlockPos(box
                .maxX(), box.maxY(), box.minZ()), new BlockPos(box
                .maxX(), box.maxY(), box.maxZ()));
    }

    @Shadow
    public abstract Structure.StructureSettings getModifiedStructureSettings();

    @Inject(method = {"generate"}, at = {@At("RETURN")}, cancellable = true)
    private void checkOtherStructuresNearby(RegistryAccess registryAccess, ChunkGenerator generator, BiomeSource biomeSource, RandomState p_226600_, StructureTemplateManager p_226601_, long p_226602_, ChunkPos p_226603_, int p_226604_, LevelHeightAccessor p_226605_, Predicate<Holder<Biome>> p_226606_, CallbackInfoReturnable<StructureStart> cir) {
        String name;
        if (cir.getReturnValue() == StructureStart.INVALID_START || !SpicedCiderConfig.COMMON.minimumStructureDistanceEnabled.get()) {
            return;
        }

        if (!(generator instanceof IGeneratorNearbyStructureHolder nearbyStructureHolder)) {
            return;
        }

        int distance = SpicedCiderConfig.COMMON.minimumStructureDistance.get();
        int xzOffset = 3000000 * distance;
        int yOffset = (getModifiedStructureSettings().step() == GenerationStep.Decoration.SURFACE_STRUCTURES) ? 2000 : 500;


        ResourceLocation regID = ((Registry) registryAccess.registry(Registries.STRUCTURE).get()).getKey(this);
        if (regID != null) {
            name = regID.toString();
        } else {
            name = "unknown:" + this;
        }

        for (StructurePiece piece : cir.getReturnValue().getPieces()) {
            BlockPos center = piece.getLocatorPosition();
            String nearby = nearbyStructureHolder.getNearby(BlockPos.asLong((center.getX() + xzOffset) / distance, (center
                    .getY() + yOffset) / distance, (center
                    .getZ() + xzOffset) / distance));

            if (nearby == null && (piece.getBoundingBox().getXSpan() > 8 || piece.getBoundingBox().getYSpan() > 8 || piece.getBoundingBox().getZSpan() > 8)) {
                for (BlockPos pos : getBoundingBoxCorners(piece.getBoundingBox())) {

                    nearby = nearbyStructureHolder.getNearby(BlockPos.asLong((pos.getX() + xzOffset) / distance, (pos
                            .getY() + yOffset) / distance, (pos
                            .getZ() + xzOffset) / distance));

                    if (nearby != null && !nearby.equals(name)) {
                        break;
                    }
                }
            }


            if (nearby != null && !nearby.equals(name)) {
                cir.setReturnValue(StructureStart.INVALID_START);

                return;
            }
        }

        for (StructurePiece piece : cir.getReturnValue().getPieces()) {
            BlockPos center = piece.getLocatorPosition();
            nearbyStructureHolder.setNearby(BlockPos.asLong((center.getX() + xzOffset) / distance, (center.getY() + 2000) / distance, (center.getZ() + xzOffset) / distance), name);

            if (piece.getBoundingBox().getXSpan() > 8 || piece.getBoundingBox().getYSpan() > 8 || piece.getBoundingBox().getZSpan() > 8) {
                for (BlockPos pos : getBoundingBoxCorners(piece.getBoundingBox())) {
                    nearbyStructureHolder.setNearby(BlockPos.asLong((pos.getX() + xzOffset) / distance, (pos.getY() + 2000) / distance, (pos.getZ() + xzOffset) / distance), name);
                }
            }
        }
    }
}