package com.evandev.spicedcider.compat.stools;

import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.quark.content.building.block.StoolBlock;

public class QuarkStools {
    public static boolean isStool(BlockState state) {
        return state.getBlock() instanceof StoolBlock;
    }

    public static boolean isBig(BlockState state) {
        return state.hasProperty(StoolBlock.BIG) && state.getValue(StoolBlock.BIG);
    }

    public static boolean isPlayerSeated(BlockState state) {
        return state.hasProperty(StoolBlock.SAT_IN) && state.getValue(StoolBlock.SAT_IN);
    }
}
