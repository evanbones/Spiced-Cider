package com.evandev.spicedcider.lodgen;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

record LodTarget(UUID player, ServerLevel level, int chunkX, int chunkZ, int radius, int builtDistance, boolean host, LongSet done) {

    boolean inRange(int x, int z) {
        long dx = x - chunkX;
        long dz = z - chunkZ;
        return dx * dx + dz * dz <= (long) radius * radius
                && !ChunkTrackingView.isWithinDistance(chunkX, chunkZ, builtDistance, x, z, false);
    }

    boolean wants(int x, int z) {
        return inRange(x, z) && !done.contains(ChunkPos.asLong(x, z));
    }
}
