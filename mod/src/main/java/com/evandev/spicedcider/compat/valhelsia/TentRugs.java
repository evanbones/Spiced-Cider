package com.evandev.spicedcider.compat.valhelsia;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.tazer.offgrid.world.Layers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;

public class TentRugs {
    public static final VoxelShape SHAPE = Block.box(0.0, 2.0, 0.0, 16.0, 16.0, 16.0);
    private static final boolean OFFGRID = ModList.get().isLoaded("offgrid");

    public static boolean enabled() {
        return OFFGRID && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.tentRugs, false);
    }

    public static boolean hasRug(BlockGetter level, BlockPos pos) {
        return enabled() && level instanceof Level world && Layers.layerIn(world, pos) != null;
    }
}
