package com.evandev.spicedcider.compat.polytone;

public final class ClientChunkEpoch {

    private static int epoch;

    public static int get() {
        return epoch;
    }

    public static void bump() {
        epoch++;
    }
}
