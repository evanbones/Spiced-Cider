package com.evandev.spicedcider.mixin.blockgrid;

import com.evandev.spicedcider.blockgrid.ChunkOffsetMap;
import com.evandev.spicedcider.blockgrid.storage.ChunkOffsetHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunk.class)
public class LevelChunkMixin implements ChunkOffsetHolder {
    @Unique
    private ChunkOffsetMap spicedcider$blockOffsets = new ChunkOffsetMap();

    @Override
    public ChunkOffsetMap spicedcider$getBlockOffsets() {
        return spicedcider$blockOffsets;
    }

    @Override
    public void spicedcider$setBlockOffsets(ChunkOffsetMap offsets) {
        this.spicedcider$blockOffsets = offsets != null ? offsets : new ChunkOffsetMap();
    }
}
