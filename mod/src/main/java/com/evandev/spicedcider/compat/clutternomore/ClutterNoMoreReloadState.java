package com.evandev.spicedcider.compat.clutternomore;

public final class ClutterNoMoreReloadState {
    private static volatile boolean missedTextures = false;
    private static volatile boolean hasRetried = false;

    private ClutterNoMoreReloadState() {
    }

    public static void reset() {
        missedTextures = false;
    }

    public static void markMissedTextures() {
        missedTextures = true;
    }

    public static boolean consumeShouldRetry() {
        boolean shouldRetry = missedTextures && !hasRetried;
        missedTextures = false;
        if (shouldRetry) hasRetried = true;
        return shouldRetry;
    }
}
