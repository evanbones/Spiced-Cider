package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.worldgen.MineshaftEntrance;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MineshaftStructure.class)
public class MineshaftEntranceMixin {
    @Shadow
    @Final
    private MineshaftStructure.Type type;

    @Inject(method = "generatePiecesAndAdjust", at = @At("RETURN"))
    private void spicedcider$addEntrance(StructurePiecesBuilder builder, Structure.GenerationContext context,
                                         CallbackInfoReturnable<Integer> cir,
                                         @Local MineshaftPieces.MineShaftRoom room) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.mineshaftEntrances, false)) return;
        RandomSource random = RandomSource.create(context.seed() ^ context.chunkPos().toLong());
        MineshaftEntrance entrance = MineshaftEntrance.create(type, room.getBoundingBox(), context.chunkGenerator(),
                context.heightAccessor(), context.randomState(), random);
        if (entrance != null) {
            builder.addPiece(entrance);
        }
    }
}
