package com.evandev.spicedcider.util;

import net.minecraft.world.level.biome.Climate;

import java.lang.ref.WeakReference;

public final class ClimateSampleCache {
    private static final ThreadLocal<ClimateSampleCache> LAST = ThreadLocal.withInitial(ClimateSampleCache::new);

    private WeakReference<Object> sampler = new WeakReference<>(null);
    private int x;
    private int y;
    private int z;
    private Climate.TargetPoint point;

    private ClimateSampleCache() {
    }

    public static ClimateSampleCache get() {
        return LAST.get();
    }

    public Climate.TargetPoint lookup(Object owner, int x, int y, int z) {
        if (point != null && this.x == x && this.y == y && this.z == z && sampler.get() == owner) return point;
        return null;
    }

    public void store(Object owner, int x, int y, int z, Climate.TargetPoint point) {
        if (sampler.get() != owner) sampler = new WeakReference<>(owner);
        this.x = x;
        this.y = y;
        this.z = z;
        this.point = point;
    }
}
