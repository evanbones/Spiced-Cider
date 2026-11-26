package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.waterlogging.BrokenBlockWaterFill;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeWaterFillMixin {
    @WrapMethod(method = "destroyBlock")
    private boolean spicedcider$fillWithWater(BlockPos pos, Operation<Boolean> original) {
        return BrokenBlockWaterFill.breaking(pos, () -> original.call(pos));
    }
}
