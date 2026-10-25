package com.evandev.spicedcider.mixin.voxyworldgen;

import com.ethan.voxyworldgenv2.core.DistanceGraph;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = DistanceGraph.class, remap = false)
public class DistanceGraphMarkMixin {

    @WrapMethod(method = "markChunkCompleted")
    private void spicedcider$serializeMarks(int chunkX, int chunkZ, Operation<Void> original) {
        synchronized (this) {
            original.call(chunkX, chunkZ);
        }
    }
}
