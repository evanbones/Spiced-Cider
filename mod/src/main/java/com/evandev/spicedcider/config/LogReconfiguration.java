package com.evandev.spicedcider.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry;

import java.io.IOException;

public final class LogReconfiguration {
    public static final Logger LOGGER = LogManager.getLogger("Spiced Cider");
    private static final long BUNDLE_ID = 54321;

    private LogReconfiguration() {
    }

    public static void run() {
        LOGGER.debug("Starting Log4j reconfiguration.");
        PluginRegistry.getInstance().loadFromBundle(BUNDLE_ID, LogReconfiguration.class.getClassLoader());
        try {
            Reconfigurator.reconfigureWithUri(ConfigFileHandler.getOrCreateDefaultConfigFile());
        } catch (UnsupportedOperationException | IOException e) {
            LOGGER.error("Failed to reconfigure Log4j:", e);
        }
        LOGGER.debug("Finished Log4j reconfiguration.");
    }
}
