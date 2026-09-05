package com.evandev.spicedcider.compat.yacl;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class ConfigScreenLoader {

    private ConfigScreenLoader() {
    }

    public static void register(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, screen) -> SpicedCiderConfigScreen.create(screen));
    }
}
