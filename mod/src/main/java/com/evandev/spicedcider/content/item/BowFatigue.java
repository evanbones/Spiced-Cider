package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.config.SpicedCiderConfig;

public class BowFatigue {
    private static final float RESTED = 0.5F;
    private static final float EXHAUSTED = 10.5F;
    private static final int FATIGUE_START_TICKS = 60;
    private static final int FATIGUE_END_TICKS = 200;
    private static final float INACCURACY_SCALE = 0.25F;

    public static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.bowFatigue, false);
    }

    public static float fatigue(float drawTicks) {
        if (drawTicks < FATIGUE_START_TICKS) return RESTED;
        if (drawTicks >= FATIGUE_END_TICKS) return EXHAUSTED;
        return RESTED + (EXHAUSTED - RESTED) * (drawTicks - FATIGUE_START_TICKS) / (FATIGUE_END_TICKS - FATIGUE_START_TICKS);
    }

    public static float inaccuracy(float base, int drawTicks) {
        return enabled() ? base * INACCURACY_SCALE * fatigue(drawTicks) : base;
    }

    public static boolean canCrit(boolean crit, int drawTicks) {
        return crit && (!enabled() || fatigue(drawTicks) <= RESTED);
    }
}
