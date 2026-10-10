package com.evandev.spicedcider.client.devbridge;

import com.google.gson.JsonObject;

import java.util.Arrays;

public final class FrameStats {
    private static final int CAPACITY = 1 << 16;

    private final long[] frameNanos = new long[CAPACITY];
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
            frameNanos[head] = now - lastFrameStart;
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
        lastFrameStart = 0;
        windowStart = System.nanoTime();
    }

    public JsonObject snapshot() {
        long[] sorted;
        long windowNanos;
        int withScreen;
        synchronized (this) {
            withScreen = screenFrames;
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
        int spikes = 0;
        for (long n : sorted) if (n > 50_000_000L) spikes++;
        out.addProperty("framesOver50ms", spikes);
        return out;
    }

    private static double percentile(long[] sorted, double p) {
        int index = (int) Math.ceil(p * sorted.length) - 1;
        return sorted[Math.max(0, Math.min(sorted.length - 1, index))] / 1e6;
    }
}
