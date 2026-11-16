package com.evandev.spicedcider.compat.treeliable;

public class LeafFelling {
    private static int depth;

    public static boolean active() {
        return depth > 0;
    }

    public static void run(Runnable action) {
        depth++;
        try {
            action.run();
        } finally {
            depth--;
        }
    }
}
