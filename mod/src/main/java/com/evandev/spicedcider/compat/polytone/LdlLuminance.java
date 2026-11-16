package com.evandev.spicedcider.compat.polytone;

import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;

final class LdlLuminance {
    private static Object instance;
    private static Object manager;
    private static Method getMode;
    private static Method isEnabled;
    private static Method getLuminance;
    private static boolean failed;

    static int of(ItemStack stack, boolean submerged) {
        if (failed) return 0;
        try {
            if (getLuminance == null) {
                Class<?> ldl = Class.forName("dev.lambdaurora.lambdynlights.LambDynLights");
                instance = ldl.getMethod("get").invoke(null);
                manager = ldl.getMethod("itemLightSourceManager").invoke(instance);
                Object config = ldl.getField("config").get(instance);
                getMode = config.getClass().getMethod("getDynamicLightsMode");
                isEnabled = getMode.getReturnType().getMethod("isEnabled");
                getLuminance = Class.forName("dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager")
                        .getMethod("getLuminance", ItemStack.class, boolean.class);
            }
            Object config = instance.getClass().getField("config").get(instance);
            if (!(Boolean) isEnabled.invoke(getMode.invoke(config))) return 0;
            return (Integer) getLuminance.invoke(manager, stack, submerged);
        } catch (ReflectiveOperationException | RuntimeException e) {
            failed = true;
            return 0;
        }
    }
}
