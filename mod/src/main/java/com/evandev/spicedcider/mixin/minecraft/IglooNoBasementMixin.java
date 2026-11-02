package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IglooPieces.class)
public class IglooNoBasementMixin {
    @WrapOperation(method = "addPieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextDouble()D"))
    private static double spicedcider$noBasement(RandomSource random, Operation<Double> original) {
        double roll = original.call(random);
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.iglooBasementRemoval, false) ? 1.0 : roll;
    }
}
