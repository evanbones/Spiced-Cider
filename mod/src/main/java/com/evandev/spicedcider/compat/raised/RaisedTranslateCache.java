package com.evandev.spicedcider.compat.raised;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

public final class RaisedTranslateCache {
    private static final MethodHandle OPTIONS = optionsGetter();
    private static final Map<String, Integer> X = new HashMap<>();
    private static final Map<String, Integer> Y = new HashMap<>();
    private static Object owner;

    public static synchronized int x(String layerName, Operation<Integer> original) {
        return lookup(X, layerName, original);
    }

    public static synchronized int y(String layerName, Operation<Integer> original) {
        return lookup(Y, layerName, original);
    }

    public static synchronized void invalidate() {
        owner = null;
        X.clear();
        Y.clear();
    }

    private static int lookup(Map<String, Integer> map, String layerName, Operation<Integer> original) {
        Object options = currentOptions();
        if (options == null || layerName == null) return original.call(layerName);
        if (options != owner) {
            X.clear();
            Y.clear();
            owner = options;
        }
        Integer cached = map.get(layerName);
        if (cached != null) return cached;
        int value = original.call(layerName);
        map.put(layerName, value);
        return value;
    }

    private static Object currentOptions() {
        if (OPTIONS == null) return null;
        try {
            return (Object) OPTIONS.invokeExact();
        } catch (Throwable t) {
            return null;
        }
    }

    private static MethodHandle optionsGetter() {
        try {
            Class<?> config = Class.forName("dev.yurisuika.raised.config.Config");
            Class<?> options = Class.forName("dev.yurisuika.raised.option.Options");
            return MethodHandles.publicLookup()
                    .findStaticGetter(config, "options", options)
                    .asType(MethodType.methodType(Object.class));
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
