package com.evandev.spicedcider.compat.yacl;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class ConfigScreenLoader {

    private static final String YACL_MOD_ID = "yet_another_config_lib_v3";

    private ConfigScreenLoader() {
    }

    public static void register(ModContainer container) {
        if (!ModList.get().isLoaded(YACL_MOD_ID)) {
            return;
        }
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, screen) -> SpicedCiderConfigScreen.create(screen));
    }
}
