package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.registries.worldgen.ModBiomes;
import com.blackgear.vanillabackport.common.registries.blocks.ModBlocks;
import com.blackgear.vanillabackport.common.registries.worldgen.ModNoises;
import com.blackgear.vanillabackport.common.worldgen.surface.ModSurfaceRuleData;
import com.blackgear.vanillabackport.common.level.worldgen.surface.SpatialNoiseThresholdConditionSource;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@IfModLoaded("vanillabackport")
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
        SurfaceRules.RuleSource bedrock = SurfaceRules.ifTrue(
                SurfaceRules.verticalGradient("bedrock", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
                SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())
        );

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.DAPPLED_FOREST),
                        SurfaceRules.ifTrue(SurfaceRules.noiseCondition(ModNoises.SMALL_PATCH, 1.2F), SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState()))
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.isBiome(ModBiomes.SULFUR_CAVES),
                        SurfaceRules.sequence(
                                bedrock,
                                SurfaceRules.ifTrue(cider$noiseCondition3d(ModNoises.SULFUR_CAVE_GRADIENT, -0.48D, -0.4D), tuff),
                                SurfaceRules.ifTrue(cider$noiseCondition3d(ModNoises.SULFUR_CAVE_GRADIENT, 0.4D, 0.48D), tuff),
                                sulfur
                        )
                )
        );
    }

    @Unique
    private static SurfaceRules.ConditionSource cider$noiseCondition3d(ResourceKey<NormalNoise.NoiseParameters> noise, double minRange, double maxRange) {
        return new SpatialNoiseThresholdConditionSource(noise, minRange, maxRange);
    }
}
