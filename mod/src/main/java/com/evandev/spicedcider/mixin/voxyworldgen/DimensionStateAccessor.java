package com.evandev.spicedcider.mixin.voxyworldgen;

import com.ethan.voxyworldgenv2.core.DistanceGraph;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("voxyworldgenv2")
@Mixin(targets = "com.ethan.voxyworldgenv2.core.ChunkGenerationManager$DimensionState", remap = false)
public interface DimensionStateAccessor {

    @Accessor("level")
    ServerLevel getLevel();

    @Accessor("distanceGraph")
    DistanceGraph getDistanceGraph();

    @Accessor("tellusActive")
    boolean isTellusActive();
}
