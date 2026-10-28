package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.registry.ModFluids;
import com.farcr.nomansland.common.event.CommonSetupEvents;
import com.farcr.nomansland.common.registry.NMLFluids;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.RegisterCauldronFluidContentEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = CommonSetupEvents.class, remap = false)
public class ResinOilCauldronFluidMixin {

    @WrapOperation(method = "registerCauldronFluidContent", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/fluids/RegisterCauldronFluidContentEvent;register(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/material/Fluid;ILnet/minecraft/world/level/block/state/properties/IntegerProperty;)V"))
    private static void spicedcider$flowingResinOil(RegisterCauldronFluidContentEvent event, Block block, Fluid fluid, int amount, IntegerProperty level, Operation<Void> original) {
        original.call(event, block, fluid == NMLFluids.RESIN_OIL.get() ? ModFluids.RESIN_OIL.get() : fluid, amount, level);
    }
}
