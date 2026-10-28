package com.evandev.spicedcider.compat.remi;

import com.evandev.remi.feature.creativemodetab.gui.CreativeModeTabGui;
import com.evandev.remi.integration.emi.ScreenManager;
import dev.emi.emi.config.SidebarSide;

public final class RemiRightTabs {

    public static boolean active() {
        var panel = ScreenManager.getTargetCreativeTabPanel();
        return panel != null && panel.side == SidebarSide.RIGHT && CreativeModeTabGui.currentTheme() == CreativeModeTabGui.TabTheme.VERTICAL;
    }
}
