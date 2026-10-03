package com.evandev.spicedcider.compat.voxy;

import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;

public final class VoxyBridge {
    private VoxyBridge() {
    }

    public static float lodDistance() {
        VoxyConfig config = VoxyConfig.CONFIG;
        if (config == null || !config.isRenderingEnabled() || IGetVoxyRenderSystem.getNullable() == null) return -1;
        return config.sectionRenderDistance * 512.0F;
    }
}
