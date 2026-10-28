package com.evandev.spicedcider.mixin.remi;

import com.evandev.remi.config.ReliableEmiConfig;
import com.evandev.remi.feature.creativemodetab.gui.CreativeModeTabGui;
import com.evandev.remi.integration.emi.ScreenManager;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.config.SidebarSide;
import dev.emi.emi.screen.EmiScreenManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("remi")
@Mixin(value = EmiScreenManager.class, remap = false)
public class EmiScreenManagerRightTabsMixin {
    private static final int TAB_SPACE = 35;

    @ModifyVariable(method = "createScreenSpace", at = @At("HEAD"), argsOnly = true)
    private static Bounds spicedcider$reserveTabsOnRight(Bounds bounds, @Local(ordinal = 0, argsOnly = true) EmiScreenManager.SidebarPanel panel) {
        if (panel.side == SidebarSide.RIGHT && ReliableEmiConfig.isCreativeTabsEnabled(panel.getType())
                && ScreenManager.getTargetCreativeTabPanel() == panel
                && CreativeModeTabGui.currentTheme() == CreativeModeTabGui.TabTheme.VERTICAL) {
            return new Bounds(bounds.x() - TAB_SPACE, bounds.y(), bounds.width(), bounds.height());
        }
        return bounds;
    }
}
