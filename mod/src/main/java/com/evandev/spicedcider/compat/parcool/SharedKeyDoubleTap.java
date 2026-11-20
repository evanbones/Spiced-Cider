package com.evandev.spicedcider.compat.parcool;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class SharedKeyDoubleTap {
    private static final int WINDOW_TICKS = 7;
    private static final Set<String> GATED = Set.of("key.parcool.crawl", "key.parcool.dodge");
    private static final Map<KeyMapping, State> STATES = new IdentityHashMap<>();

    public static boolean gate(KeyMapping key, boolean down) {
        if (!GATED.contains(key.getName())) return down;
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.parcoolDoubleTapSharedKeys, false)) return down;
        Options options = Minecraft.getInstance().options;
        if (!key.same(options.keyShift) && !key.same(options.keySprint)) return down;

        State state = STATES.computeIfAbsent(key, k -> new State());
        if (state.ticksSinceRelease < WINDOW_TICKS + 1) state.ticksSinceRelease++;
        if (down && !state.wasDown) {
            state.latched = state.ticksSinceRelease <= WINDOW_TICKS;
        } else if (!down && state.wasDown) {
            state.ticksSinceRelease = state.latched ? WINDOW_TICKS + 1 : 0;
            state.latched = false;
        }
        state.wasDown = down;
        return down && state.latched;
    }

    private static class State {
        private boolean wasDown;
        private boolean latched;
        private int ticksSinceRelease = WINDOW_TICKS + 1;
    }
}
