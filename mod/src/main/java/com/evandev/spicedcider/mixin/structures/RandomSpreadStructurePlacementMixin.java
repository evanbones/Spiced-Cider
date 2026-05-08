package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin({RandomSpreadStructurePlacement.class})
public class RandomSpreadStructurePlacementMixin {
    @Shadow
    @Final
    @Mutable
    private int spacing;
    @Shadow
    @Final
    @Mutable
    private int separation;

    @Inject(method = {"<init>(Lnet/minecraft/core/Vec3i;Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement$FrequencyReductionMethod;FILjava/util/Optional;IILnet/minecraft/world/level/levelgen/structure/placement/RandomSpreadType;)V"}, at = {@At("RETURN")})
    private void adjustSpacingSeperation(Vec3i p_227000_, StructurePlacement.FrequencyReductionMethod p_227001_, float p_227002_, int p_227003_, Optional p_227004_, int p_227005_, int p_227006_, RandomSpreadType p_227007_, CallbackInfo ci) {
        this.spacing = Mth.clamp((int) Math.round(this.spacing * SpicedCiderConfig.COMMON.spacingSeparationModifier.get()), 1, 4095);
        this.separation = Mth.clamp((int) Math.round(this.separation * SpicedCiderConfig.COMMON.spacingSeparationModifier.get()), 0, 4095);
        if (this.spacing <= this.separation) {
            this.spacing = this.separation + 1;
        }
    }
}