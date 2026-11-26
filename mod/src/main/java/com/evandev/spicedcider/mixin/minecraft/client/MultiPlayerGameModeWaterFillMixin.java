package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.content.waterlogging.BrokenBlockWaterFill;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeWaterFillMixin {
    @WrapMethod(method = "destroyBlock")
    private boolean spicedcider$fillWithWater(BlockPos pos, Operation<Boolean> original) {
        return BrokenBlockWaterFill.breaking(pos, () -> original.call(pos));
    }
}
