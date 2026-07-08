package com.evandev.spicedcider.mixin.redstone;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @Inject(
            method = "getNeighborSignal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"),
            cancellable = true
    )
    private void spicedcider$disablePistonQC(SignalGetter signalGetter, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (SpicedCiderConfig.COMMON.removeQuasiConnectivity.get()) {
            cir.setReturnValue(false);
        }
    }
}