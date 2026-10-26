package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.CopperWindChimeBlock;
import com.evandev.spicedcider.compat.chimes.ChimesWindClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("chimes")
@IfModLoaded("livingwind")
@Mixin(value = CopperWindChimeBlock.class, remap = false)
public class CopperWindChimeSoundWindMixin {

    @ModifyExpressionValue(method = "animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int spicedcider$windChance(int chance, @Local(argsOnly = true) BlockPos pos) {
        float factor = ChimesWindClient.soundFactor(pos);
        return factor <= 0.0F ? Integer.MAX_VALUE : Math.max(1, Math.round(chance / factor));
    }
}
