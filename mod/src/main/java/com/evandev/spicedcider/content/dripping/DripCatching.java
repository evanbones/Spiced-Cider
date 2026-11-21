package com.evandev.spicedcider.content.dripping;

import com.evandev.spicedcider.compat.create.BasinEvaporation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class DripCatching {
    public static final int MAX_DISTANCE = 11;
    public static final int WATER_PER_DRIP = 250;
    public static final int LAVA_PER_DRIP = 1000;

    @Nullable
    public static BlockPos findCatcher(Level level, BlockPos from) {
        BlockPos.MutableBlockPos cursor = from.mutable();
        for (int i = 0; i < MAX_DISTANCE; i++) {
            cursor.move(Direction.DOWN);
            if (level.getBlockState(cursor).isAir()) continue;
            BlockPos target = cursor.immutable();
            return level.getCapability(Capabilities.FluidHandler.BLOCK, target, Direction.UP) != null ? target : null;
        }
        return null;
    }

    public static boolean fill(ServerLevel level, BlockPos target, FluidStack stack) {
        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, target, Direction.UP);
        if (handler == null || handler.fill(stack, IFluidHandler.FluidAction.SIMULATE) <= 0) return false;
        handler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    public static void dripstone(ServerLevel level, BlockPos tip, Fluid fluid) {
        BlockPos target = findCatcher(level, tip);
        if (target == null) return;
        boolean caught;
        if (fluid == Fluids.WATER && ModList.get().isLoaded("create") && BasinEvaporation.evaporate(level, target)) {
            caught = true;
        } else {
            caught = fill(level, target, new FluidStack(fluid, fluid == Fluids.LAVA ? LAVA_PER_DRIP : WATER_PER_DRIP));
        }
        if (caught) {
            level.levelEvent(LevelEvent.DRIPSTONE_DRIP, tip, 0);
            level.levelEvent(fluid == Fluids.LAVA ? LevelEvent.SOUND_DRIP_LAVA_INTO_CAULDRON : LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, target, 0);
        }
    }
}
