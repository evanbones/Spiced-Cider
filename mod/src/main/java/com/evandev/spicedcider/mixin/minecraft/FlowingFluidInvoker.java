package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowingFluid.class)
public interface FlowingFluidInvoker {
    @Invoker("getNewLiquid")
    FluidState spicedcider$getNewLiquid(Level level, BlockPos pos, BlockState state);
}
