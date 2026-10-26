package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.IronWindChimeBlock;
import com.evandev.spicedcider.compat.chimes.ChimesWindClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("chimes")
@IfModLoaded("livingwind")
@Mixin(value = IronWindChimeBlock.class, remap = false)
public class IronWindChimeSoundWindMixin {

    @ModifyExpressionValue(method = "animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At(value = "CONSTANT", args = "floatValue=0.025"))
    private float spicedcider$windChance(float chance, @Local(argsOnly = true) BlockPos pos) {
        return chance * ChimesWindClient.soundFactor(pos);
    }
}
