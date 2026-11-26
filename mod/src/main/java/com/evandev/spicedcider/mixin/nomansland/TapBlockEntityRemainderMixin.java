package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.common.blockentity.TapBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = TapBlockEntity.class, remap = false)
public class TapBlockEntityRemainderMixin {

    @WrapOperation(method = "perform", at = @At(value = "INVOKE", target = "Lcom/farcr/nomansland/common/blockentity/TapBlockEntity;unit(Lnet/minecraft/world/level/material/Fluid;)I", ordinal = 2))
    private int spicedcider$drainRemainder(Fluid fluid, Operation<Integer> original, @Local IFluidHandler handler) {
        int unit = original.call(fluid);
        FluidStack left = handler.drain(new FluidStack(fluid, unit), IFluidHandler.FluidAction.SIMULATE);
        return left.getAmount() > 0 && left.getAmount() < unit ? left.getAmount() : unit;
    }
}
