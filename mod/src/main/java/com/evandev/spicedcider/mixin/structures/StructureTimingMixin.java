package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.command.SpicedCiderStructureCommand;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(Structure.class)
public class StructureTimingMixin {
    @Unique
    private long time = 0L;

    @Unique
    private ResourceLocation id = null;

    @Inject(method = "generate", at = @At("HEAD"))
    private void beforeGenerate(RegistryAccess p_226597_, ChunkGenerator p_226598_, BiomeSource p_226599_, RandomState p_226600_, StructureTemplateManager p_226601_, long p_226602_, ChunkPos p_226603_, int p_226604_, LevelHeightAccessor p_226605_, Predicate<Holder<Biome>> p_226606_, CallbackInfoReturnable<StructureStart> cir) {
        this.time = System.nanoTime();
    }

    @Inject(method = "generate", at = @At("RETURN"))
    private void afterGenerate(RegistryAccess registryAccess, ChunkGenerator p_226598_, BiomeSource p_226599_, RandomState p_226600_, StructureTemplateManager p_226601_, long p_226602_, ChunkPos p_226603_, int p_226604_, LevelHeightAccessor p_226605_, Predicate<Holder<Biome>> p_226606_, CallbackInfoReturnable<StructureStart> cir) {
        long elapsedTime = (System.nanoTime() - this.time) / 10L;

        if (this.id == null) {
            this.id = registryAccess.registryOrThrow(Registries.STRUCTURE).getKey((Structure) (Object) this);
        }

        SpicedCiderStructureCommand.STRUCTURE_TIMINGS.merge(this.id, elapsedTime, Long::sum);
    }
}