package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.AmethystWindChimeBlock;
import com.evandev.spicedcider.compat.chimes.ChimesWindClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("chimes")
@IfModLoaded("livingwind")
@Mixin(value = AmethystWindChimeBlock.class, remap = false)
public class AmethystWindChimeSoundWindMixin {

    @ModifyExpressionValue(method = "animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At(value = "CONSTANT", args = "doubleValue=0.015"))
    private double spicedcider$windChance(double chance, @Local(argsOnly = true) BlockPos pos) {
        return chance * ChimesWindClient.soundFactor(pos);
    }
}
