package com.evandev.spicedcider.compat.modernfix;

import net.minecraft.server.level.ServerLevel;
import org.embeddedt.modernfix.duck.IChunkGenerator;

public final class ModernFixLeaks {
    public static void release(ServerLevel level) {
        if (level.getChunkSource().getGeneratorState() instanceof IChunkGenerator state) {
            state.mfix$setStrongholdCachePath(null, null);
        }
    }
}
