package com.evandev.spicedcider.lodgen;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;

final class LodPacer {
    private static final long TARGET_DELAY_NANOS = 25_000_000L;
    private static final long BASE_HISTORY_NANOS = 10_000_000_000L;
    private static final double MIN_WINDOW = 1.0;
    private static final double MAX_WINDOW = 64.0;
    private static final double START_WINDOW = 2.0;
    private static final double BACKOFF = 0.7;

    private final LongArrayFIFOQueue inFlight = new LongArrayFIFOQueue();
    private double window = START_WINDOW;
    private long currentMin = Long.MAX_VALUE;
    private long previousMin = Long.MAX_VALUE;
    private long historyStart = System.nanoTime();
    private long lastBackoff;
    private long lastRtt;

    synchronized boolean canSend() {
        return inFlight.size() < (int) window;
    }

    synchronized void sent() {
        inFlight.enqueue(System.nanoTime());
    }

    synchronized void ack() {
        if (inFlight.isEmpty()) return;
        long now = System.nanoTime();
        long rtt = now - inFlight.dequeueLong();
        lastRtt = rtt;
        if (now - historyStart > BASE_HISTORY_NANOS) {
            previousMin = currentMin;
            currentMin = Long.MAX_VALUE;
            historyStart = now;
        }
        currentMin = Math.min(currentMin, rtt);
        long base = Math.min(previousMin, currentMin);
        if (rtt - base > TARGET_DELAY_NANOS) {
            if (now - lastBackoff > rtt) {
                window = Math.max(MIN_WINDOW, window * BACKOFF);
                lastBackoff = now;
            }
        } else {
            window = Math.min(MAX_WINDOW, window + 1.0 / window);
        }
    }

    synchronized String describe() {
        long base = Math.min(previousMin, currentMin);
        return String.format("window %.1f, in flight %d, rtt %d ms, base %s ms", window, inFlight.size(), lastRtt / 1_000_000L,
                base == Long.MAX_VALUE ? "?" : String.valueOf(base / 1_000_000L));
    }
}
