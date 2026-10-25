package com.evandev.spicedcider.compat.exposure;

import com.evandev.spicedcider.mixin.leaks.ExposureServerAccessor;

public final class ExposureLeaks {
    public static void release() {
        ExposureServerAccessor.spicedcider$setVault(null);
        ExposureServerAccessor.spicedcider$setFrameHistory(null);
    }
}
