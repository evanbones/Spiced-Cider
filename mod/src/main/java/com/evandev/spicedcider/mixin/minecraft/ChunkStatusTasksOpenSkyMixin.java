package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.worldgen.OpenSkyCaveBiomes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(targets = "net.minecraft.world.level.chunk.status.ChunkStatusTasks")
public class ChunkStatusTasksOpenSkyMixin {

    @ModifyReturnValue(method = "generateNoise", at = @At("RETURN"))
    private static CompletableFuture<ChunkAccess> spicedcider$coverCaveBiomes(CompletableFuture<ChunkAccess> original, @Local(argsOnly = true) WorldGenContext context) {
        return original.thenApply(chunk -> OpenSkyCaveBiomes.apply(context.level(), chunk));
    }
}
