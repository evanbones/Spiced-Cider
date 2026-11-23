package com.evandev.spicedcider.client.gui;

public class TooltipRendering {
    private static int depth;

    public static void enter() {
        depth++;
    }

    public static void exit() {
        depth--;
    }

    public static boolean active() {
        return depth > 0;
    }
}
