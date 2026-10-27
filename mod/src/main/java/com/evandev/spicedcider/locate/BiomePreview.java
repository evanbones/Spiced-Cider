package com.evandev.spicedcider.locate;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public final class BiomePreview {
    private static final int SIZE = 128;
    private static final int MIN_SLICE = 64;
    private static final ThreadLocal<BiomeManager> ACTIVE = new ThreadLocal<>();

    public static BiomeManager active() {
        return ACTIVE.get();
    }

    public static void begin(ServerLevel level, int centerX, int centerZ, int scale) {
        ACTIVE.set(prepare(level, centerX, centerZ, scale));
    }

    public static void end() {
        ACTIVE.remove();
    }

    private static BiomeManager prepare(ServerLevel level, int centerX, int centerZ, int scale) {
        int step = 1 << scale;
        int left = centerX / step - SIZE / 2;
        int top = centerZ / step - SIZE / 2;
        LongLinkedOpenHashSet quarts = new LongLinkedOpenHashSet();
        BiomeManager recorder = level.getBiomeManager().withDifferentSource((x, y, z) -> {
            quarts.add(BlockPos.asLong(x, y, z));
            return null;
        });
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                recorder.getBiome(pos.set((left + column) * step, 0, (top + row) * step));
            }
        }
        long[] keys = quarts.toLongArray();
        @SuppressWarnings("unchecked")
        Holder<Biome>[] biomes = new Holder[keys.length];
        IntArrayList uncached = new IntArrayList();
        for (int i = 0; i < keys.length; i++) {
            int x = BlockPos.getX(keys[i]);
            int z = BlockPos.getZ(keys[i]);
            ChunkAccess chunk = level.getChunk(QuartPos.toSection(x), QuartPos.toSection(z), ChunkStatus.BIOMES, false);
            if (chunk != null) biomes[i] = chunk.getNoiseBiome(x, BlockPos.getY(keys[i]), z);
            else uncached.add(i);
        }
        LocateWorkers.forEachSlice(uncached.size(), MIN_SLICE, (from, to) -> {
            for (int n = from; n < to; n++) {
                int i = uncached.getInt(n);
                biomes[i] = level.getUncachedNoiseBiome(BlockPos.getX(keys[i]), BlockPos.getY(keys[i]), BlockPos.getZ(keys[i]));
            }
        });
        Long2ObjectOpenHashMap<Holder<Biome>> resolved = new Long2ObjectOpenHashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) resolved.put(keys[i], biomes[i]);
        return level.getBiomeManager().withDifferentSource((x, y, z) -> {
            Holder<Biome> biome = resolved.get(BlockPos.asLong(x, y, z));
            return biome != null ? biome : level.getNoiseBiome(x, y, z);
        });
    }
}
