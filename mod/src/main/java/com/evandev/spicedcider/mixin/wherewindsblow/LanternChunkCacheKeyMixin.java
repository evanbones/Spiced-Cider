package com.evandev.spicedcider.mixin.wherewindsblow;

import com.jvn.wherewindsblow.client.lantern.SwingingLanternAssemblyRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("where_winds_blow")
@Mixin(value = SwingingLanternAssemblyRenderer.class, remap = false)
public class LanternChunkCacheKeyMixin {

    @WrapOperation(method = {"hangingLanternsForChunk", "invalidateChunk(Lnet/minecraft/world/level/chunk/LevelChunk;)V"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ChunkPos;toLong()J"))
    private static long spicedcider$mixChunkKey(ChunkPos pos, Operation<Long> original) {
        return HashCommon.mix(original.call(pos));
    }

    @WrapOperation(method = "invalidateChunk(Lnet/minecraft/core/BlockPos;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ChunkPos;asLong(Lnet/minecraft/core/BlockPos;)J"))
    private static long spicedcider$mixBlockChunkKey(BlockPos pos, Operation<Long> original) {
        return HashCommon.mix(original.call(pos));
    }
}
