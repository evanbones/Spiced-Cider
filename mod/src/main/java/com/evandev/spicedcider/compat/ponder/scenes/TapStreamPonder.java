package com.evandev.spicedcider.compat.ponder.scenes;

import com.evandev.spicedcider.compat.nomansland.tap.TapFluidHolder;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;

final class TapStreamPonder {
    static void show(BlockEntity tap, Fluid fluid, float length) {
        if (tap instanceof TapFluidHolder holder && tap.getLevel() != null) {
            holder.spicedcider$fluids().show(fluid == null ? null : SoftFluidStack.fromFluid(fluid, 1, tap.getLevel().registryAccess()), length);
        }
    }
}
