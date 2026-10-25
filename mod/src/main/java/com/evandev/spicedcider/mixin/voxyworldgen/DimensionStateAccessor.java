package com.evandev.spicedcider.mixin.voxyworldgen;

import com.ethan.voxyworldgenv2.core.DistanceGraph;
import it.unimi.dsi.fastutil.longs.LongSet;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@IfModLoaded("voxyworldgenv2")
@Mixin(targets = "com.ethan.voxyworldgenv2.core.ChunkGenerationManager$DimensionState", remap = false)
public interface DimensionStateAccessor {

    @Accessor("level")
    ServerLevel getLevel();

    @Accessor("distanceGraph")
    DistanceGraph getDistanceGraph();

    @Accessor("completedChunks")
    LongSet getCompletedChunks();

    @Accessor("trackedBatches")
    Set<Long> getTrackedBatches();

    @Accessor("tellusActive")
    boolean isTellusActive();
}
