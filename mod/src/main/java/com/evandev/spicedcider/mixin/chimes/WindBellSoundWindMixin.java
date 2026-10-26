package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.WindBellBlock;
import com.evandev.spicedcider.compat.chimes.ChimesWindClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("chimes")
@IfModLoaded("livingwind")
@Mixin(value = WindBellBlock.class, remap = false)
public class WindBellSoundWindMixin {

    @ModifyExpressionValue(method = "animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At(value = "CONSTANT", args = "doubleValue=0.25"))
    private double spicedcider$windChance(double chance, @Local(argsOnly = true) BlockPos pos) {
        return chance * ChimesWindClient.soundFactor(pos);
    }
}
