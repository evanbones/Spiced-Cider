package com.evandev.spicedcider.locate;

import com.evandev.spicedcider.SpicedCider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public final class LocateWorkers {
    private static final int THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static volatile ExecutorService pool;

    static void forEachSlice(int count, int minSlice, SliceRange range) {
        int slices = Math.max(1, Math.min(THREADS * 4, count / Math.max(1, minSlice)));
        int size = (count + slices - 1) / slices;
        List<Future<?>> futures = new ArrayList<>(slices);
        ExecutorService executor = pool();
        for (int start = 0; start < count; start += size) {
            int from = start;
            int to = Math.min(count, start + size);
            futures.add(executor.submit(() -> range.run(from, to)));
        }
        Throwable failure = null;
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                if (failure == null) failure = e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (failure == null) failure = e;
            }
        }
        if (failure != null) throw new LocateFailure(failure);
    }

    private static ExecutorService pool() {
        ExecutorService executor = pool;
        if (executor == null) {
            synchronized (LocateWorkers.class) {
                executor = pool;
                if (executor == null) {
                    ClassLoader loader = LocateWorkers.class.getClassLoader();
                    AtomicInteger count = new AtomicInteger();
                    executor = Executors.newFixedThreadPool(THREADS, runnable -> {
                        Thread thread = new Thread(runnable, "Spiced Cider locate " + count.incrementAndGet());
                        thread.setDaemon(true);
                        thread.setContextClassLoader(loader);
                        return thread;
                    });
                    pool = executor;
                }
            }
        }
        return executor;
    }

    public static void logFallback(Throwable failure) {
        SpicedCider.LOGGER.error("Parallel locate failed, falling back to the vanilla search", failure);
    }

    @FunctionalInterface
    interface SliceRange {
        void run(int from, int to);
    }

    public static final class LocateFailure extends RuntimeException {
        LocateFailure(Throwable cause) {
            super(cause);
        }
    }
}
