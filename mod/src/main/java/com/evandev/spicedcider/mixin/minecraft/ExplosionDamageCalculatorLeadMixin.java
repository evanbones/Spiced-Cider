package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.registry.ModLeadBlocks;
import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ExplosionDamageCalculator.class)
public class ExplosionDamageCalculatorLeadMixin {
    @ModifyReturnValue(method = "getBlockExplosionResistance", at = @At("RETURN"))
    private Optional<Float> spicedcider$leadResistance(Optional<Float> resistance, Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {
        if (!state.is(ModTags.Blocks.BLAST_RESISTANT_LEAD)) return resistance;
        return Optional.of(Math.max(resistance.orElse(0.0F), ModLeadBlocks.RESISTANCE));
    }
}
