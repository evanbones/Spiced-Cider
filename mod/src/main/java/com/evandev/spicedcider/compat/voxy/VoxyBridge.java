package com.evandev.spicedcider.compat.voxy;

import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;

public final class VoxyBridge {
    public static float lodDistance() {
        VoxyConfig config = VoxyConfig.CONFIG;
        if (config == null || !config.isRenderingEnabled() || IGetVoxyRenderSystem.getNullable() == null) return -1;
        return config.sectionRenderDistance * 512.0F;
    }

    public static int lodRadiusChunks() {
        VoxyConfig config = VoxyConfig.CONFIG;
        if (config == null || !config.isRenderingEnabled()) return 0;
        return (int) Math.ceil(config.sectionRenderDistance * 32.0F);
    }
}
