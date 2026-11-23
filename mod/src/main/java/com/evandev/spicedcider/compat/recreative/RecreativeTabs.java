package com.evandev.spicedcider.compat.recreative;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.util.Map;

public class RecreativeTabs {
    private static Map<?, ?> customTabs;
    private static boolean resolved;

    public static boolean isClientOnlyTab(ResourceLocation id) {
        Map<?, ?> tabs = customTabs();
        return tabs != null && id != null && tabs.containsKey(id.toString());
    }

    private static Map<?, ?> customTabs() {
        if (!resolved) {
            resolved = true;
            if (ModList.get().isLoaded("recreative")) {
                try {
                    customTabs = (Map<?, ?>) Class.forName("com.evandev.recreative.data.CreativeTabManager").getField("CUSTOM_TABS_DEFS").get(null);
                } catch (ReflectiveOperationException | ClassCastException ignored) {
                }
            }
        }
        return customTabs;
    }
}
