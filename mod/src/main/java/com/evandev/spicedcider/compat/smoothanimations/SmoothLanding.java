package com.evandev.spicedcider.compat.smoothanimations;

import java.lang.reflect.Method;

public final class SmoothLanding {
    private static final Method IS_LANDING = find();

    private static Method find() {
        try {
            return Class.forName("net.mehvahdjukaar.smooth_animations.common.LandingFallingBlock").getMethod("smoothSP$isLanding");
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

    public static boolean isLanding(Object entity) {
        if (IS_LANDING == null || !IS_LANDING.getDeclaringClass().isInstance(entity)) return false;
        try {
            return (boolean) IS_LANDING.invoke(entity);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }
}
