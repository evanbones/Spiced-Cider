package com.evandev.spicedcider.mixin.blockgrid;

import com.evandev.spicedcider.blockgrid.ChunkOffsetMap;
import com.evandev.spicedcider.blockgrid.storage.ChunkOffsetHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Inject(method = "write", at = @At("RETURN"))
    private static void spicedcider$writeChunkOffsets(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        if (chunk instanceof ChunkOffsetHolder holder) {
            ChunkOffsetMap offsets = holder.spicedcider$getBlockOffsets();
            if (offsets != null && !offsets.isEmpty()) {
                ChunkOffsetMap.CODEC.encodeStart(NbtOps.INSTANCE, offsets)
                        .result()
                        .ifPresent(offsetTag -> tag.put("spicedcider:block_offsets", offsetTag));
            }
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void spicedcider$readChunkOffsets(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
        if (tag.contains("spicedcider:block_offsets")) {
            ChunkOffsetMap.CODEC.parse(NbtOps.INSTANCE, tag.get("spicedcider:block_offsets"))
                    .result()
                    .ifPresent(offsets -> {
                        ChunkAccess chunk = cir.getReturnValue();
                        if (chunk instanceof ChunkOffsetHolder holder) {
                            holder.spicedcider$setBlockOffsets(offsets);
                        }
                    });
        }
    }
}
