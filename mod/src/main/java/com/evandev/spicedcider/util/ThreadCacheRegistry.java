package com.evandev.spicedcider.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ThreadCacheRegistry {
    private static final List<WeakReference<Map<?, ?>>> MAPS = new ArrayList<>();
    private static final ThreadLocal<Map<?, ?>> REGISTERED = new ThreadLocal<>();

    private ThreadCacheRegistry() {
    }

    public static void track(Map<?, ?> map) {
        if (REGISTERED.get() == map) return;
        REGISTERED.set(map);
        synchronized (MAPS) {
            MAPS.removeIf(ref -> ref.get() == null);
            MAPS.add(new WeakReference<>(map));
        }
    }

    public static void clearAll() {
        synchronized (MAPS) {
            for (WeakReference<Map<?, ?>> ref : MAPS) {
                Map<?, ?> map = ref.get();
                if (map != null) map.clear();
            }
        }
    }
}
