package com.evandev.spicedcider.voidwater;

import net.minecraft.world.level.ChunkPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoidTrailColumns {
    private final Map<Long, byte[]> chunks = new ConcurrentHashMap<>();

    public static int index(int x, int z) {
        return (z & 15) << 4 | (x & 15);
    }

    public int get(int x, int z) {
        byte[] lengths = chunks.get(ChunkPos.asLong(x >> 4, z >> 4));
        return lengths == null ? 0 : lengths[index(x, z)] & 0xFF;
    }

    public boolean set(int x, int z, int length) {
        long key = ChunkPos.asLong(x >> 4, z >> 4);
        byte[] lengths = chunks.get(key);
        if (lengths == null) {
            if (length == 0) return false;
            lengths = new byte[256];
            chunks.put(key, lengths);
        }
        int i = index(x, z);
        if ((lengths[i] & 0xFF) == length) return false;
        lengths[i] = (byte) length;
        if (length == 0 && isEmpty(lengths)) chunks.remove(key);
        return true;
    }

    public byte[] chunk(long key) {
        return chunks.get(key);
    }

    public void putChunk(long key, byte[] lengths) {
        chunks.put(key, lengths);
    }

    public void removeChunk(long key) {
        chunks.remove(key);
    }

    public Map<Long, byte[]> chunks() {
        return chunks;
    }

    private static boolean isEmpty(byte[] lengths) {
        for (byte length : lengths) {
            if (length != 0) return false;
        }
        return true;
    }
}
