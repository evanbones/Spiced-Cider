package com.evandev.spicedcider.content.block.glass;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PaneConnections {
    private static final int[] CTM_INDEX = new int[256];

    static {
        int[] masks = {0, 4, 68, 64, 20, 80, 21, 84, 87, 93, 245, 215, 16, 28, 124, 112, 5, 65, 69, 81, 213, 117, 125, 95,
                17, 31, 255, 241, 29, 116, 23, 92, 247, 223, 119, 221, 1, 7, 199, 193, 71, 209, 197, 113, 253, 127, 85};
        for (int i = 0; i < masks.length; i++) {
            CTM_INDEX[masks[i]] = i;
        }
    }

    @Nullable
    public static Direction fullPaneAxis(BlockState state) {
        boolean north = state.getValue(IronBarsBlock.NORTH);
        boolean south = state.getValue(IronBarsBlock.SOUTH);
        boolean west = state.getValue(IronBarsBlock.WEST);
        boolean east = state.getValue(IronBarsBlock.EAST);
        if (north && south && !west && !east) return Direction.NORTH;
        if (west && east && !north && !south) return Direction.EAST;
        return null;
    }

    public static boolean continues(Block block, BlockState other, Direction axis) {
        return other.is(block) && fullPaneAxis(other) == axis;
    }

    public static boolean sameConnections(Block block, BlockState state, BlockState other) {
        return other.is(block)
                && state.getValue(IronBarsBlock.NORTH) == other.getValue(IronBarsBlock.NORTH)
                && state.getValue(IronBarsBlock.SOUTH) == other.getValue(IronBarsBlock.SOUTH)
                && state.getValue(IronBarsBlock.WEST) == other.getValue(IronBarsBlock.WEST)
                && state.getValue(IronBarsBlock.EAST) == other.getValue(IronBarsBlock.EAST);
    }

    public static int vertical(boolean up, boolean down) {
        if (up) return down ? 2 : 3;
        return down ? 1 : 0;
    }

    public static int eightway(boolean top, boolean topRight, boolean right, boolean bottomRight,
                               boolean bottom, boolean bottomLeft, boolean left, boolean topLeft) {
        int mask = 0;
        if (top) mask |= 1;
        if (right) mask |= 1 << 2;
        if (bottom) mask |= 1 << 4;
        if (left) mask |= 1 << 6;
        if (top && right && topRight) mask |= 1 << 1;
        if (right && bottom && bottomRight) mask |= 1 << 3;
        if (bottom && left && bottomLeft) mask |= 1 << 5;
        if (left && top && topLeft) mask |= 1 << 7;
        return CTM_INDEX[mask];
    }
}
