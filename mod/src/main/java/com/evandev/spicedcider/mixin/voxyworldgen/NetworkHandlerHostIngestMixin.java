package com.evandev.spicedcider.mixin.voxyworldgen;

import com.evandev.spicedcider.compat.voxyworldgen.VoxyWorldGenHostIngest;
import com.ethan.voxyworldgenv2.network.NetworkHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = NetworkHandler.class, remap = false)
public class NetworkHandlerHostIngestMixin {

    @WrapMethod(method = "broadcastLODData(Lnet/minecraft/world/level/chunk/LevelChunk;)V")
    private static void spicedcider$markServerIngested(LevelChunk chunk, Operation<Void> original) {
        VoxyWorldGenHostIngest.begin();
        try {
            original.call(chunk);
        } finally {
            VoxyWorldGenHostIngest.end();
        }
    }

    @WrapOperation(
            method = "broadcastLODData(Lnet/minecraft/world/level/chunk/LevelChunk;Lit/unimi/dsi/fastutil/ints/IntSet;)V",
            at = @At(value = "INVOKE",
                    target = "Lcom/ethan/voxyworldgenv2/network/NetworkHandler;sendAsync(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/ChunkPos;ILjava/util/List;Ljava/util/List;)V")
    )
    private static void spicedcider$skipHostCopy(ResourceKey<Level> dim, ChunkPos pos, int minY, List<NetworkHandler.LODDataPayload.SectionData> sections,
                                                 List<ServerPlayer> recipients, Operation<Void> original) {
        List<ServerPlayer> remaining = VoxyWorldGenHostIngest.withoutHost(recipients);
        if (!remaining.isEmpty()) original.call(dim, pos, minY, sections, remaining);
    }
}
