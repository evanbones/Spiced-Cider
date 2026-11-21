package com.evandev.spicedcider.util;

import com.evandev.spicedcider.config.SpicedCiderConfig;

public class BundleCapacity {
    public static int get() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.bundleCapacity, 64);
    }
}
