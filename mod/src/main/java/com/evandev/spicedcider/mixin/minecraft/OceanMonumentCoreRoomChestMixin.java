package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.worldgen.MonumentChests;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OceanMonumentPieces.OceanMonumentCoreRoom.class)
public abstract class OceanMonumentCoreRoomChestMixin extends StructurePiece {

    protected OceanMonumentCoreRoomChestMixin(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
    }

    @Inject(method = "postProcess", at = @At("TAIL"))
    private void spicedcider$addChests(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random,
                                       BoundingBox box, ChunkPos chunkPos, BlockPos pos, CallbackInfo ci) {
        this.spicedcider$chest(level, box, random, 7, 1, 3, Direction.SOUTH);
        this.spicedcider$chest(level, box, random, 8, 1, 12, Direction.NORTH);
    }

    private void spicedcider$chest(WorldGenLevel level, BoundingBox box, RandomSource random, int x, int y, int z, Direction facing) {
        this.placeBlock(level, MonumentChests.chest(facing), x, y, z, box);
        MonumentChests.fill(level, box, random, this.getWorldPos(x, y, z));
    }
}
