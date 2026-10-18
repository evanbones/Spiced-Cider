package com.evandev.spicedcider.compat.nomansland;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.Arrays;

public final class VeinNoiseField {
    private static final int XZ_CELL_SCALE = 2;
    private static final int Y_CELL_SCALE = 2;
    private static final int XZ_CELL_MASK = (1 << XZ_CELL_SCALE) - 1;
    private static final int Y_CELL_MASK = (1 << Y_CELL_SCALE) - 1;
    private static final double INV_XZ_CELL_SIZE = 1.0 / (double) (1 << XZ_CELL_SCALE);
    private static final double INV_Y_CELL_SIZE = 1.0 / (double) (1 << Y_CELL_SCALE);
    private static final int XZ_CELL_COUNT = (16 >> XZ_CELL_SCALE) + 1;
    private static final int XZ_CELL_STRIDE = XZ_CELL_COUNT * XZ_CELL_COUNT;
    private static final ThreadLocal<VeinNoiseField> CACHED = new ThreadLocal<>();

    private final int chunkHeight;
    private final double[] veinA;
    private final double[] veinB;
    private final double[] gap;
    private final int[] cellStamp;
    private final double[] columnA;
    private final double[] columnB;
    private final double[] columnGap;
    private final int[] columnStamp;
    private boolean inUse;
    private int fillStamp;
    private int sampleStamp;
    private int firstLayer;
    private int lastLayer;
    private int minX;
    private int minY;
    private int minZ;
    private NormalNoise noiseA;
    private NormalNoise noiseB;
    private NormalNoise noiseGap;

    private VeinNoiseField(int chunkHeight) {
        this.chunkHeight = chunkHeight;
        int cells = XZ_CELL_STRIDE * ((chunkHeight >> Y_CELL_SCALE) + 1);
        this.veinA = new double[cells];
        this.veinB = new double[cells];
        this.gap = new double[cells];
        this.cellStamp = new int[cells];
        this.columnA = new double[chunkHeight];
        this.columnB = new double[chunkHeight];
        this.columnGap = new double[chunkHeight];
        this.columnStamp = new int[chunkHeight];
    }

    public static VeinNoiseField acquire(int chunkHeight) {
        VeinNoiseField field = CACHED.get();
        if (field == null || field.chunkHeight != chunkHeight) {
            field = new VeinNoiseField(chunkHeight);
            CACHED.set(field);
        } else if (field.inUse) {
            field = new VeinNoiseField(chunkHeight);
        }
        field.inUse = true;
        return field;
    }

    public void release() {
        inUse = false;
        noiseA = null;
        noiseB = null;
        noiseGap = null;
    }

    public void begin(int startY, int endY, int minX, int minY, int minZ, NormalNoise noiseA, NormalNoise noiseB, NormalNoise noiseGap) {
        int yCellCount = (chunkHeight >> Y_CELL_SCALE) + 1;
        this.firstLayer = Math.max(0, startY >> Y_CELL_SCALE);
        this.lastLayer = Math.min(yCellCount - 1, endY >> Y_CELL_SCALE);
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.noiseA = noiseA;
        this.noiseB = noiseB;
        this.noiseGap = noiseGap;
        if (++fillStamp == 0) {
            Arrays.fill(cellStamp, 0);
            fillStamp = 1;
        }
    }

    public void beginColumn() {
        if (++sampleStamp == 0) {
            Arrays.fill(columnStamp, 0);
            sampleStamp = 1;
        }
    }

    public double veinA(int localY) {
        return columnA[localY];
    }

    public double veinB(int localY) {
        return columnB[localY];
    }

    public double gap(int localY) {
        return columnGap[localY];
    }

    public void sample(int x, int y, int z) {
        if (columnStamp[y] == sampleStamp) return;
        columnStamp[y] = sampleStamp;

        int cellX = x >> XZ_CELL_SCALE;
        int cellY = y >> Y_CELL_SCALE;
        int cellZ = z >> XZ_CELL_SCALE;
        int localX = x & XZ_CELL_MASK;
        int localY = y & Y_CELL_MASK;
        int localZ = z & XZ_CELL_MASK;
        double interpX = (double) localX * INV_XZ_CELL_SIZE;
        double interpY = (double) localY * INV_Y_CELL_SIZE;
        double interpZ = (double) localZ * INV_XZ_CELL_SIZE;
        int nextX = cellX + ((localX | -localX) >>> 31);
        int nextY = cellY + ((localY | -localY) >>> 31);
        int nextZ = cellZ + ((localZ | -localZ) >>> 31);

        int i000 = cell(cellX, cellY, cellZ);
        int i100 = cell(nextX, cellY, cellZ);
        int i001 = cell(cellX, cellY, nextZ);
        int i101 = cell(nextX, cellY, nextZ);
        int i010 = cell(cellX, nextY, cellZ);
        int i110 = cell(nextX, nextY, cellZ);
        int i011 = cell(cellX, nextY, nextZ);
        int i111 = cell(nextX, nextY, nextZ);

        columnA[y] = Mth.lerp3(interpX, interpZ, interpY,
                veinA[i000], veinA[i100], veinA[i001], veinA[i101], veinA[i010], veinA[i110], veinA[i011], veinA[i111]);
        columnB[y] = Mth.lerp3(interpX, interpZ, interpY,
                veinB[i000], veinB[i100], veinB[i001], veinB[i101], veinB[i010], veinB[i110], veinB[i011], veinB[i111]);
        columnGap[y] = Mth.lerp3(interpX, interpZ, interpY,
                gap[i000], gap[i100], gap[i001], gap[i101], gap[i010], gap[i110], gap[i011], gap[i111]);
    }

    private int cell(int cellX, int cellY, int cellZ) {
        int index = cellX + cellZ * XZ_CELL_COUNT + cellY * XZ_CELL_STRIDE;
        if (cellStamp[index] != fillStamp) {
            cellStamp[index] = fillStamp;
            if (cellY >= firstLayer && cellY <= lastLayer) {
                int globalX = (cellX << XZ_CELL_SCALE) + minX;
                int globalY = (cellY << Y_CELL_SCALE) + minY;
                int globalZ = (cellZ << XZ_CELL_SCALE) + minZ;
                veinA[index] = noiseA.getValue((double) (globalX * 4), (double) (globalY * 4), (double) (globalZ * 4));
                veinB[index] = noiseB.getValue((double) (globalX * 4), (double) (globalY * 4), (double) (globalZ * 4));
                gap[index] = noiseGap.getValue((double) globalX, (double) globalY, (double) globalZ);
            } else {
                veinA[index] = 0.0;
                veinB[index] = 0.0;
                gap[index] = 0.0;
            }
        }
        return index;
    }
}
