package com.evandev.spicedcider.mixin.redstone;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @ModifyExpressionValue(
            method = "neighborChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z", ordinal = 1)
    )
    private boolean spicedcider$disableDispenserQC(boolean original) {
        if (SpicedCiderConfig.COMMON.removeQuasiConnectivity.get()) {
            return false;
        }
        return original;
    }
}