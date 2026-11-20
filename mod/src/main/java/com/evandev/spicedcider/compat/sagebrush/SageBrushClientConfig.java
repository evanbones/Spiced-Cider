package com.evandev.spicedcider.compat.sagebrush;

import com.evandev.spicedcider.SpicedCider;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class SageBrushClientConfig {
    private static volatile Object clientSpec;
    private static volatile boolean resolved;

    public static boolean isUnloadedOnServer(ModConfigSpec spec) {
        if (!FMLEnvironment.dist.isDedicatedServer()) return false;
        if (!resolved) {
            try {
                clientSpec = Class.forName("com.davigj.sage_brush.core.SBConfig").getField("CLIENT_SPEC").get(null);
            } catch (ReflectiveOperationException | LinkageError e) {
                SpicedCider.LOGGER.warn("Could not find Sage Brush client config spec", e);
            }
            resolved = true;
        }
        return spec == clientSpec;
    }
}
