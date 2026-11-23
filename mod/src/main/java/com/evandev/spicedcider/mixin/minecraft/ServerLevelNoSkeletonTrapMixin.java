package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelNoSkeletonTrapMixin {
    @ModifyExpressionValue(method = "tickChunk", at = @At(value = "CONSTANT",
            args = "classValue=net/minecraft/world/level/block/LightningRodBlock"))
    private boolean spicedcider$noSkeletonTrap(boolean onLightningRod) {
        return onLightningRod || SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.noSkeletonTrapHorses, false);
    }
}
