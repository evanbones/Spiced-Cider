package com.evandev.spicedcider.compat.cirrus;

import com.evandev.spicedcider.mixin.leaks.CirrusTimeTransitionAccessor;

public final class CirrusLeaks {
    public static void releaseClient() {
        CirrusTimeTransitionAccessor.spicedcider$setTrackedLevel(null);
    }
}
