package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.registries.ModBiomes;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.blackgear.vanillabackport.common.registries.ModNoises;
import com.blackgear.vanillabackport.common.worldgen.ModSurfaceRuleData;
import com.blackgear.vanillabackport.common.worldgen.surface.SpatialNoiseThresholdConditionSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ModSurfaceRuleData.class, remap = false)
public class ModSurfaceRuleDataMixin {

    /**
     * @author Evan
     * @reason Rework Sulphur Caves surface rules:
     * - Tuff outer transition line [-0.48, -0.4] and [0.4, 0.48]
     * - Sulfur across the rest of the biome
     */
    @Overwrite
    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.RuleSource sulfur = SurfaceRules.state(ModBlocks.SULFUR.get().defaultBlockState());
        SurfaceRules.RuleSource tuff = SurfaceRules.state(Blocks.TUFF.defaultBlockState());

        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(ModBiomes.SULFUR_CAVES),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(cider$noiseCondition3d(ModNoises.SULFUR_CAVE_GRADIENT, -0.48D, -0.4D), tuff),
                        SurfaceRules.ifTrue(cider$noiseCondition3d(ModNoises.SULFUR_CAVE_GRADIENT, 0.4D, 0.48D), tuff),
                        sulfur
                )
        );
    }

    @Unique
    private static SurfaceRules.ConditionSource cider$noiseCondition3d(ResourceKey<NormalNoise.NoiseParameters> noise, double minRange, double maxRange) {
        return new SpatialNoiseThresholdConditionSource(noise, minRange, maxRange);
    }
}
