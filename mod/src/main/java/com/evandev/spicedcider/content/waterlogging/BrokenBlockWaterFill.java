package com.evandev.spicedcider.content.waterlogging;

import com.evandev.spicedcider.mixin.minecraft.FlowingFluidInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

public final class BrokenBlockWaterFill {
    private static final ThreadLocal<BlockPos> BREAKING = new ThreadLocal<>();
    private static final AtomicInteger ACTIVE = new AtomicInteger();

    public static boolean breaking(BlockPos pos, BooleanSupplier destroy) {
        BlockPos previous = BREAKING.get();
        BREAKING.set(pos.immutable());
        ACTIVE.incrementAndGet();
        try {
            return destroy.getAsBoolean();
        } finally {
            ACTIVE.decrementAndGet();
            if (previous == null) BREAKING.remove();
            else BREAKING.set(previous);
        }
    }

    public static BlockState fill(Level level, BlockPos pos, BlockState state) {
        if (ACTIVE.get() == 0 || !state.isAir() || !pos.equals(BREAKING.get())) return state;
        FluidState water = ((FlowingFluidInvoker) Fluids.WATER).spicedcider$getNewLiquid(level, pos, state);
        return water.isEmpty() ? state : water.createLegacyBlock();
    }
}
