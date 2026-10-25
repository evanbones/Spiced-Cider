package com.evandev.spicedcider.compat.nomansland;

import com.farcr.nomansland.common.extension.ChunkGeneratorExtension;
import net.minecraft.server.level.ServerLevel;

public final class NoMansLandLeaks {
    public static void release(ServerLevel level) {
        if (level.getChunkSource().getGenerator() instanceof ChunkGeneratorExtension generator) {
            generator.nomansland$setStructureState(null);
        }
    }
}
