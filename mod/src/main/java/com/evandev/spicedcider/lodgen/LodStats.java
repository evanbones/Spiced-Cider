package com.evandev.spicedcider.lodgen;

import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLongArray;

final class LodStats {
    static final int LIGHT = ChunkStatus.getStatusList().size();
    static final int DISK = LIGHT + 1;
    private static final AtomicLongArray NANOS = new AtomicLongArray(DISK + 1);
    private static final AtomicLongArray COUNTS = new AtomicLongArray(DISK + 1);

    static void add(int slot, long nanos, int count) {
        NANOS.addAndGet(slot, nanos);
        COUNTS.addAndGet(slot, count);
    }

    static void reset() {
        for (int i = 0; i < NANOS.length(); i++) {
            NANOS.set(i, 0);
            COUNTS.set(i, 0);
        }
    }

    static List<String> lines() {
        List<String> lines = new ArrayList<>();
        long total = 0;
        for (int i = 0; i < NANOS.length(); i++) total += NANOS.get(i);
        if (total == 0) return lines;
        for (int i = 0; i < NANOS.length(); i++) {
            long count = COUNTS.get(i);
            if (count == 0) continue;
            String name = i == LIGHT ? "light" : i == DISK ? "disk" : ChunkStatus.getStatusList().get(i).getName();
            lines.add(String.format(Locale.ROOT, "  %s: %d, %.2f ms each, %.0f%% of time",
                    name, count, NANOS.get(i) / 1e6 / count, 100.0 * NANOS.get(i) / total));
        }
        return lines;
    }
}
