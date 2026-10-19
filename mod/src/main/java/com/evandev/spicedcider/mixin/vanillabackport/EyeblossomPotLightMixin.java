package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.blocks.EyeblossomPotBlock;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("vanillabackport")
@Mixin(value = EyeblossomPotBlock.class, remap = false)
public class EyeblossomPotLightMixin {

    @WrapOperation(
            method = "randomTick",
            at = @At(value = "INVOKE", target = "Lcom/blackgear/vanillabackport/core/util/WorldUtilities$EnvironmentUtils;isNaturalNight(Lnet/minecraft/world/level/Level;)Z")
    )
    private boolean spicedcider$openInDimLight(Level level, Operation<Boolean> original, BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        return level.getMaxLocalRawBrightness(pos) <= 7;
    }
}
