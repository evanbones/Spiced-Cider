package com.evandev.spicedcider.wind.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;

public final class WindExposure {
    private static final int SIZE = 128;
    private static final int MASK = SIZE - 1;
    private static final int HALF = SIZE / 2;
    private static final int ROWS_PER_TICK = 8;
    private static final float ENCLOSED_EXPOSURE = 0.7F;
    private static final int[][] RING = {
            {3, 0}, {-3, 0}, {0, 3}, {0, -3}, {2, 2}, {-2, 2}, {2, -2}, {-2, -2},
            {6, 0}, {-6, 0}, {0, 6}, {0, -6}
    };

    private static final short[] ROOFS = new short[SIZE * SIZE];
    private static volatile int centerX;
    private static volatile int centerZ;
    private static volatile boolean ready;
    private static volatile boolean enclosed;
    private static int nextRow;
    private static int rowsFilled;

    public static float at(double x, double y, double z) {
        if (enclosed) return ENCLOSED_EXPOSURE;
        if (!ready) return 1.0F;
        int blockX = Mth.floor(x);
        int blockZ = Mth.floor(z);
        if (Math.abs(blockX - centerX) >= HALF - 6 || Math.abs(blockZ - centerZ) >= HALF - 6) return 1.0F;
        float level = (float) y + 0.5F;
        float open = level >= roof(blockX, blockZ) ? 4.0F : 0.0F;
        for (int[] offset : RING) {
            if (level >= roof(blockX + offset[0], blockZ + offset[1])) open += 1.0F;
        }
        float fraction = open / (RING.length + 4.0F);
        float eased = Mth.clamp((fraction - 0.15F) / 0.5F, 0.0F, 1.0F);
        return eased * eased * (3.0F - 2.0F * eased);
    }

    static void refresh(ClientLevel level, int cameraX, int cameraZ) {
        enclosed = level.dimensionType().hasCeiling();
        if (enclosed) return;
        if (Math.abs(cameraX - centerX) > HALF / 2 || Math.abs(cameraZ - centerZ) > HALF / 2) {
            ready = false;
            rowsFilled = 0;
        }
        centerX = cameraX;
        centerZ = cameraZ;
        for (int i = 0; i < ROWS_PER_TICK; i++) {
            int z = cameraZ - HALF + nextRow;
            for (int x = cameraX - HALF; x < cameraX + HALF; x++) {
                ROOFS[index(x, z)] = (short) level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            }
            nextRow = (nextRow + 1) & MASK;
        }
        if (!ready && (rowsFilled += ROWS_PER_TICK) >= SIZE) ready = true;
    }

    static void reset() {
        ready = false;
        rowsFilled = 0;
    }

    private static int roof(int x, int z) {
        return ROOFS[index(x, z)];
    }

    private static int index(int x, int z) {
        return (x & MASK) | (z & MASK) << 7;
    }
}
