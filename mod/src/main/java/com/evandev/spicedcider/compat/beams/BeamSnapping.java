package com.evandev.spicedcider.compat.beams;

import com.evandev.spicedcider.config.SpicedCiderConfig;

public final class BeamSnapping {
    public static final int[] GRID = {0, 2, 4};

    public static boolean gridEnabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.beamGridSnapping, false);
    }

    public static int[] snap(int[] uvd) {
        if (!gridEnabled()) return uvd;
        return new int[]{nearest(uvd[0]), nearest(uvd[1]), uvd[2]};
    }

    private static int nearest(int value) {
        return value <= 1 ? 0 : value >= 3 ? 4 : 2;
    }
}
