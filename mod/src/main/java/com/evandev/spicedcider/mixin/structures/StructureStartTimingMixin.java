package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.command.SpicedCiderStructureCommand;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public abstract class StructureStartTimingMixin {
    @Unique
    private long time = 0L;

    @Unique
    private ResourceLocation id = null;

    @Shadow
    public abstract Structure getStructure();

    @Inject(method = "placeInChunk", at = @At("HEAD"))
    private void beforeGenerate(WorldGenLevel p_226851_, StructureManager p_226852_, ChunkGenerator p_226853_, RandomSource p_226854_, BoundingBox p_226855_, ChunkPos p_226856_, CallbackInfo ci) {
        this.time = System.nanoTime();
    }

    @Inject(method = "placeInChunk", at = @At("RETURN"))
    private void afterGenerate(WorldGenLevel worldGenLevel, StructureManager p_226852_, ChunkGenerator p_226853_, RandomSource p_226854_, BoundingBox p_226855_, ChunkPos p_226856_, CallbackInfo ci) {
        long elapsedTime = (System.nanoTime() - this.time) / 10L;

        if (this.id == null) {
            this.id = worldGenLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(getStructure());
        }

        if (this.id != null) {
            SpicedCiderStructureCommand.STRUCTURE_TIMINGS.merge(this.id, elapsedTime, Long::sum);
        }
    }
}