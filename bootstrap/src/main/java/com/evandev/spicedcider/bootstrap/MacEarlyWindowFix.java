package com.evandev.spicedcider.bootstrap;

import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper;

import java.util.Locale;

public class MacEarlyWindowFix implements GraphicsBootstrapper {
    private static final String DRIPPY_PROVIDER = "drippy_early_window";

    @Override
    public String name() {
        return "spicedcider_mac_early_window";
    }

    @Override
    public void bootstrap(String[] arguments) {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("mac")) return;
        try {
            if (!FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)) return;
            if (!DRIPPY_PROVIDER.equals(FMLConfig.getConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_PROVIDER))) return;
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL, false);
            System.out.println("Disabled the Drippy early loading window on macOS, where it crashes (see Drippy-Loading-Screen#126).");
        } catch (Throwable ignored) {
        }
    }
}
