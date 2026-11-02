package com.evandev.spicedcider.compat.wherewindsblow;

import java.util.IdentityHashMap;
import java.util.Map;

public final class WindStreakSegmentGates {
    private static final int SEGMENTS = 24;
    private static final Map<Object, long[]> GATES = new IdentityHashMap<>();

    private static long[] current;
    private static long currentBit;
    private static boolean recording;
    private static boolean bypassing;
    private static boolean blocked;

    public static boolean begin(Object streak, float t0, long tick) {
        long[] gate = GATES.computeIfAbsent(streak, key -> new long[3]);
        if (gate[0] != tick) {
            gate[0] = tick;
            gate[1] = 0L;
            gate[2] = 0L;
        }
        long bit = 1L << Math.round(t0 * SEGMENTS);
        if ((gate[1] & bit) != 0L) {
            recording = false;
            if ((gate[2] & bit) != 0L) {
                bypassing = false;
                return false;
            }
            bypassing = true;
            return true;
        }
        current = gate;
        currentBit = bit;
        recording = true;
        bypassing = false;
        blocked = false;
        return true;
    }

    public static void end() {
        if (recording && current != null) {
            current[1] |= currentBit;
            if (blocked) current[2] |= currentBit;
        }
        recording = false;
        bypassing = false;
        current = null;
    }

    public static boolean bypassing() {
        return bypassing;
    }

    public static void record(boolean clear) {
        if (recording && !clear) blocked = true;
    }
}
