package com.evandev.spicedcider.client.window;

import com.evandev.spicedcider.config.EarlyStartupConfig;
import com.evandev.spicedcider.config.SpicedCiderConfig;

public final class WindowIconConfig {

    public static boolean enabled() {
        if (SpicedCiderConfig.STARTUP_SPEC.isLoaded()) {
            return SpicedCiderConfig.STARTUP.customWindowIcon.get();
        }
        return EarlyStartupConfig.get("customWindowIcon");
    }
}
