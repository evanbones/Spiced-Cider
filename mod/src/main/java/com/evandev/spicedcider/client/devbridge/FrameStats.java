package com.evandev.spicedcider.client.devbridge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;

public final class FrameStats {
    private static final int CAPACITY = 1 << 16;

    private static final int SPIKE_CAPACITY = 512;
    private static final long SPIKE_NANOS = 50_000_000L;

    private final long[] frameNanos = new long[CAPACITY];
    private final long[] spikeAt = new long[SPIKE_CAPACITY];
    private final long[] spikeNanos = new long[SPIKE_CAPACITY];
    private int spikeCount;
    private long lastFrameStart;
    private long windowStart = System.nanoTime();
    private int count;
    private int head;
    private int screenFrames;
    private volatile long totalFrames;
    private volatile long clientTicks;

    public synchronized void onFrameStart(boolean screenOpen) {
        long now = System.nanoTime();
        if (lastFrameStart != 0) {
            if (screenOpen) screenFrames++;
            long frame = now - lastFrameStart;
            frameNanos[head] = frame;
            if (frame > SPIKE_NANOS && spikeCount < SPIKE_CAPACITY) {
                spikeAt[spikeCount] = lastFrameStart - windowStart;
                spikeNanos[spikeCount] = frame;
                spikeCount++;
            }
            head = (head + 1) & (CAPACITY - 1);
            if (count < CAPACITY) count++;
        }
        lastFrameStart = now;
        totalFrames++;
    }

    public void onClientTick() {
        clientTicks++;
    }

    public long totalFrames() {
        return totalFrames;
    }

    public long clientTicks() {
        return clientTicks;
    }

    public synchronized void reset() {
        count = 0;
        head = 0;
        screenFrames = 0;
        spikeCount = 0;
        lastFrameStart = 0;
        windowStart = System.nanoTime();
    }

    public JsonObject snapshot() {
        long[] sorted;
        long windowNanos;
        int withScreen;
        JsonArray spikes = new JsonArray();
        long epochOffset = System.currentTimeMillis() - System.nanoTime() / 1_000_000L;
        synchronized (this) {
            withScreen = screenFrames;
            for (int i = 0; i < spikeCount; i++) {
                JsonArray spike = new JsonArray();
                spike.add(Math.round(spikeAt[i] / 1e6) / 1000.0);
                spike.add(Math.round(spikeNanos[i] / 1e5) / 10.0);
                spike.add(epochOffset + (windowStart + spikeAt[i]) / 1_000_000L);
                spikes.add(spike);
            }
            sorted = new long[count];
            for (int i = 0; i < count; i++) {
                sorted[i] = frameNanos[(head - count + i) & (CAPACITY - 1)];
            }
            windowNanos = System.nanoTime() - windowStart;
        }
        long sum = 0;
        for (long n : sorted) sum += n;
        Arrays.sort(sorted);

        JsonObject out = new JsonObject();
        out.addProperty("frames", sorted.length);
        out.addProperty("windowSeconds", windowNanos / 1e9);
        out.addProperty("framesWithScreenOpen", withScreen);
        if (sorted.length == 0) return out;

        double avgMs = sum / 1e6 / sorted.length;
        out.addProperty("avgFps", 1000.0 / avgMs);
        out.addProperty("avgMs", avgMs);
        out.addProperty("p50Ms", percentile(sorted, 0.50));
        out.addProperty("p95Ms", percentile(sorted, 0.95));
        out.addProperty("p99Ms", percentile(sorted, 0.99));
        out.addProperty("maxMs", sorted[sorted.length - 1] / 1e6);
        out.addProperty("onePercentLowFps", 1000.0 / percentile(sorted, 0.99));
        int over = 0;
        for (long n : sorted) if (n > SPIKE_NANOS) over++;
        out.addProperty("framesOver50ms", over);
        out.add("spikes", spikes);
        return out;
    }

    private static double percentile(long[] sorted, double p) {
        int index = (int) Math.ceil(p * sorted.length) - 1;
        return sorted[Math.max(0, Math.min(sorted.length - 1, index))] / 1e6;
    }
}
