package com.evandev.spicedcider.mixin.nomansland;

import com.farcr.nomansland.common.blockentity.TapBlockEntity;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@IfModLoaded("nomansland")
@Mixin(value = TapBlockEntity.class, remap = false)
public interface TapBlockEntityInvoker {

    @Invoker("putFluid")
    static boolean spicedcider$putFluid(Level level, BlockPos pos, BlockState state, FluidStack stack) {
        throw new AssertionError();
    }
}
