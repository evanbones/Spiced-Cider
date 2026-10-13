package com.evandev.spicedcider.mixin.voxyworldgen;

import com.ethan.voxyworldgenv2.core.ChunkGenerationManager;
import com.ethan.voxyworldgenv2.core.DistanceGraph;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = ChunkGenerationManager.class, remap = false)
public class ChunkGenerationManagerDispatchMixin {

    @Unique
    private static final int spicedcider$MAX_IDLE_BATCHES = 8;

    @Shadow
    @Final
    private AtomicBoolean running;

    @Shadow
    @Final
    private AtomicBoolean workerRunning;

    @Unique
    private int spicedcider$idleBatches;

    @WrapMethod(method = "dispatchGeneration")
    private boolean spicedcider$resetIdleBatches(List<ServerPlayer> players, int budget, Operation<Boolean> original) {
        spicedcider$idleBatches = 0;
        return original.call(players, budget);
    }

    @WrapOperation(
            method = "dispatchGeneration",
            at = @At(value = "INVOKE", target = "Lcom/ethan/voxyworldgenv2/core/DistanceGraph;findWork(Lnet/minecraft/world/level/ChunkPos;ILjava/util/Set;)Ljava/util/List;")
    )
    private List<ChunkPos> spicedcider$stopWhenIdleOrStopping(DistanceGraph graph, ChunkPos center, int radius,
                                                              Set<Long> trackedBatches, Operation<List<ChunkPos>> original) {
        if (!running.get() || !workerRunning.get() || spicedcider$idleBatches >= spicedcider$MAX_IDLE_BATCHES) return null;
        return original.call(graph, center, radius, trackedBatches);
    }

    @ModifyExpressionValue(
            method = "dispatchGeneration",
            at = @At(value = "INVOKE", target = "Lcom/ethan/voxyworldgenv2/core/ChunkGenerationManager;dispatchBatch(Lcom/ethan/voxyworldgenv2/core/ChunkGenerationManager$DimensionState;Ljava/util/List;I)I")
    )
    private int spicedcider$countIdleBatches(int sent) {
        if (sent <= 0) spicedcider$idleBatches++;
        return sent;
    }
}
