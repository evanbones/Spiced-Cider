package com.evandev.spicedcider.weather.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record FrostedBlockColor(BlockColor inner) implements BlockColor {

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        int color = inner.getColor(state, level, pos, tintIndex);
        if (level == null || pos == null || color == -1) return color;
        int stage = FrostedLeaves.stageAt(pos);
        if (stage == 0 || !state.is(FrostedLeaves.WHITENS_IN_SNOWFALL)) return color;
        return FrostedLeaves.frost(color, stage);
    }
}
