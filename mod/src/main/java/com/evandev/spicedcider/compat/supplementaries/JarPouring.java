package com.evandev.spicedcider.compat.supplementaries;

import net.mehvahdjukaar.supplementaries.common.block.tiles.JarBlockTile;
import net.minecraft.Util;

import java.util.Map;
import java.util.WeakHashMap;

public class JarPouring {
    private static final float LEVEL_SECONDS = 0.6F;
    private static final Map<JarBlockTile, Shown> SHOWN = new WeakHashMap<>();

    private static final class Shown {
        float height;
        long millis;
    }

    public static void markEmpty(JarBlockTile jar) {
        Shown shown = SHOWN.computeIfAbsent(jar, key -> new Shown());
        shown.height = 0.0F;
        shown.millis = Util.getMillis();
    }

    public static float shownHeight(JarBlockTile jar, float target) {
        long now = Util.getMillis();
        Shown shown = SHOWN.get(jar);
        if (shown == null) {
            shown = new Shown();
            shown.height = target;
            shown.millis = now;
            SHOWN.put(jar, shown);
            return target;
        }
        float dt = Math.min(Math.max((now - shown.millis) / 1000.0F, 0.0F), 0.25F);
        shown.millis = now;
        shown.height += (target - shown.height) * (1.0F - (float) Math.exp(-dt / LEVEL_SECONDS));
        if (Math.abs(target - shown.height) < 0.001F) shown.height = target;
        return shown.height;
    }
}
