package com.evandev.spicedcider.compat.offgrid;

import dev.tazer.offgrid.world.Cells;
import dev.tazer.offgrid.world.Overlay;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class OffgridOverlayStates {
    public static BlockState matching(BlockGetter level, BlockPos pos, BlockState real, TagKey<Block> tag) {
        if (real.is(tag)) return real;
        for (Overlay overlay : Cells.overlays(level, pos)) {
            if (overlay.getState().is(tag)) return overlay.getState();
        }
        return real;
    }
}
