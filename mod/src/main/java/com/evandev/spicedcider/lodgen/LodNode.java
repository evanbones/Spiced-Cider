package com.evandev.spicedcider.lodgen;

import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;

final class LodNode {
    final int x;
    final int z;
    final Holder holder;
    volatile ProtoChunk chunk;
    volatile boolean failed;
    boolean running;
    boolean runningFeatures;
    boolean runningStarts;
    int refs;
    int needBiomes;
    int needCarvers;
    int needFeatures;
    int unlocked;

    LodNode(int x, int z, ProtoChunk chunk) {
        this.x = x;
        this.z = z;
        this.chunk = chunk;
        this.holder = new Holder(chunk.getPos(), this);
    }

    ChunkStatus status() {
        return chunk.getPersistedStatus();
    }

    boolean reached(ChunkStatus status) {
        return !failed && status().isOrAfter(status);
    }

    ChunkStatus required() {
        if (needFeatures > 0) return ChunkStatus.FEATURES;
        if (needCarvers > 0) return ChunkStatus.CARVERS;
        if (needBiomes > 0) return ChunkStatus.BIOMES;
        return refs > 0 ? ChunkStatus.STRUCTURE_STARTS : ChunkStatus.EMPTY;
    }

    static final class Holder extends GenerationChunkHolder {
        @Nullable
        private final LodNode node;

        Holder(ChunkPos pos, @Nullable LodNode node) {
            super(pos);
            this.node = node;
        }

        @Override
        public @Nullable ChunkAccess getChunkIfPresentUnchecked(ChunkStatus status) {
            if (node == null) return null;
            ProtoChunk chunk = node.chunk;
            return chunk.getPersistedStatus().isOrAfter(status) ? chunk : null;
        }

        @Override
        public int getTicketLevel() {
            return 0;
        }

        @Override
        public int getQueueLevel() {
            return 0;
        }
    }
}
