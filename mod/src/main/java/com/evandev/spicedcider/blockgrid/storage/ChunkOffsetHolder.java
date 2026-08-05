package com.evandev.spicedcider.blockgrid.storage;

import com.evandev.spicedcider.blockgrid.ChunkOffsetMap;

public interface ChunkOffsetHolder {
    ChunkOffsetMap spicedcider$getBlockOffsets();

    void spicedcider$setBlockOffsets(ChunkOffsetMap offsets);
}
