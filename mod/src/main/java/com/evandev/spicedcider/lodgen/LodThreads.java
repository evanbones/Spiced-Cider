package com.evandev.spicedcider.lodgen;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class LodThreads {

    public static boolean isLodThread() {
        return Thread.currentThread() instanceof LodThread;
    }

    static ThreadFactory factory(String name) {
        AtomicInteger count = new AtomicInteger();
        return runnable -> create(runnable, name + " " + count.incrementAndGet());
    }

    static Thread create(Runnable runnable, String name) {
        Thread thread = new LodThread(runnable, name);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        return thread;
    }

    private static final class LodThread extends Thread {
        private LodThread(Runnable runnable, String name) {
            super(runnable, name);
        }
    }
}
