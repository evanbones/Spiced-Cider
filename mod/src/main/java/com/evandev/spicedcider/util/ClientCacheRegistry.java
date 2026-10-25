package com.evandev.spicedcider.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class ClientCacheRegistry {
    private static final List<WeakReference<Object>> CACHES = new ArrayList<>();

    public static void track(Collection<?> collection) {
        add(collection);
    }

    public static void track(Map<?, ?> map) {
        add(map);
    }

    private static synchronized void add(Object cache) {
        CACHES.removeIf(ref -> ref.get() == null);
        CACHES.add(new WeakReference<>(cache));
    }

    public static synchronized void clearAll() {
        for (WeakReference<Object> ref : CACHES) {
            Object cache = ref.get();
            if (cache instanceof Collection<?> collection) collection.clear();
            else if (cache instanceof Map<?, ?> map) map.clear();
        }
    }
}
