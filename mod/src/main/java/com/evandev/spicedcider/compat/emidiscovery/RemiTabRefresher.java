package com.evandev.spicedcider.compat.emidiscovery;

import com.evandev.remi.feature.creativemodetab.CreativeModeTabManager;
import dev.emi.emi.screen.EmiScreenManager;

public final class RemiTabRefresher {

    public static void refresh() {
        CreativeModeTabManager.reload();
        EmiScreenManager.recalculate();
    }
}
