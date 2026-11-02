package com.evandev.spicedcider.mixin.mynethersdelight;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.soytutta.mynethersdelight.common.data.PlantRuleEngine;
import com.soytutta.mynethersdelight.common.data.PlantRuleSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("mynethersdelight")
@Mixin(targets = "vectorwing.farmersdelight.common.block.RichSoilBlock", remap = false)
public class RichSoilResurgentRulesMixin {
    @Inject(method = "randomTick", at = @At("HEAD"))
    private void spicedcider$applyResurgentTransforms(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        BlockPos above = pos.above();
        PlantRuleEngine.applySoilTransformRules(level, above, level.getBlockState(above), PlantRuleSet.SoilType.RESURGENT_SOIL, random);
    }
}
