package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.command.SpicedCiderStructureCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlacedFeature.class)
public abstract class PlacedFeatureErrorMixin {

    @Shadow
    protected abstract boolean placeWithContext(PlacementContext paramPlacementContext, RandomSource paramRandomSource, BlockPos paramBlockPos);

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/placement/PlacedFeature;placeWithContext(Lnet/minecraft/world/level/levelgen/placement/PlacementContext;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean onPlace(PlacedFeature feature, PlacementContext context, RandomSource randomSource, BlockPos pos) {
        PlacedFeature self = (PlacedFeature) (Object) this;
        ResourceLocation key = context.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getKey(self);

        try {
            long prev = System.nanoTime();
            boolean result = placeWithContext(context, randomSource, pos);
            long elapsedTime = (System.nanoTime() - prev) / 10L;

            if (key != null) {
                SpicedCiderStructureCommand.FEATURE_TIMINGS.merge(key, elapsedTime, Long::sum);
            }
            return result;
        } catch (Exception e) {
            SpicedCider.LOGGER.warn("Feature: {} errored during placement at {}", key, pos);
            return false;
        }
    }

    @Redirect(method = "placeWithBiomeCheck", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/placement/PlacedFeature;placeWithContext(Lnet/minecraft/world/level/levelgen/placement/PlacementContext;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean onPlaceWithBiome(PlacedFeature feature, PlacementContext context, RandomSource randomSource, BlockPos pos) {
        PlacedFeature self = (PlacedFeature) (Object) this;
        ResourceLocation key = context.getLevel().registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getKey(self);

        try {
            long prev = System.nanoTime();
            boolean result = placeWithContext(context, randomSource, pos);
            long elapsedTime = (System.nanoTime() - prev) / 10L;

            if (key != null) {
                SpicedCiderStructureCommand.FEATURE_TIMINGS.merge(key, elapsedTime, Long::sum);
            }
            return result;
        } catch (Exception e) {
            SpicedCider.LOGGER.warn("Feature: {} errored during placement at {}", key, pos);
            return false;
        }
    }
}