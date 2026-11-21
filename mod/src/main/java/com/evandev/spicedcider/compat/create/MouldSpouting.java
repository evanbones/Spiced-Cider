package com.evandev.spicedcider.compat.create;

import com.evandev.spicedcider.content.casting.MouldBlockEntity;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MouldSpouting implements BlockSpoutingBehaviour {
    public static void register() {
        BlockSpoutingBehaviour.BY_BLOCK_ENTITY.register(ModBlockEntities.MOULD.get(), new MouldSpouting());
    }

    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack available, boolean simulate) {
        if (!(level.getBlockEntity(pos) instanceof MouldBlockEntity mould)) {
            return 0;
        }
        return mould.fill(available, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
    }
}
