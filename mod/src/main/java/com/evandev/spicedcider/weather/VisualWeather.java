package com.evandev.spicedcider.weather;

public final class VisualWeather {
    private static int depth;
    private static float overcastRain;

    public static void enter() {
        depth++;
    }

    public static void exit() {
        depth--;
    }

    public static float rainLevel(float rain) {
        return depth > 0 ? overcast(rain) : rain;
    }

    public static float overcast(float rain) {
        return Math.max(rain, overcastRain);
    }

    public static float overcastRain() {
        return overcastRain;
    }

    public static void setOvercastRain(float value) {
        overcastRain = value;
    }
}
