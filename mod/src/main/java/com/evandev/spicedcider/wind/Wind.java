package com.evandev.spicedcider.wind;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public final class Wind {

    public static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.wind, false);
    }

    public static WindSample sample(ServerLevel level) {
        return enabled() ? WindData.get(level).sample() : WindSample.CALM;
    }

    public static WindSample sample(ServerLevel level, BlockPos pos) {
        WindSample sample = sample(level);
        if (WindData.Profile.of(level) != WindData.Profile.OPEN) return sample;
        return sample.scaled(WindSample.altitudeFactor(pos.getY()));
    }

    public static boolean exposed(ServerLevel level, BlockPos pos) {
        if (level.canSeeSky(pos)) return true;
        int open = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(direction)).isAir() && level.getBlockState(pos.relative(direction, 2)).isAir()) open++;
        }
        return open >= 2;
    }
}
