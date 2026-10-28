package com.evandev.spicedcider.mixin.remi;

import com.evandev.remi.config.ReliableEmiConfig;
import com.evandev.remi.feature.creativemodetab.gui.CreativeModeTabGui;
import com.evandev.remi.gui.components.ImageButton;
import com.evandev.remi.gui.components.ScrollbarWidget;
import com.evandev.remi.integration.emi.ScreenManager;
import com.evandev.spicedcider.compat.remi.RemiRightTabs;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.config.SidebarTheme;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("remi")
@Mixin(value = CreativeModeTabGui.class, remap = false)
public class CreativeModeTabGuiRightTabsMixin {

    @Shadow
    @Final
    private static ImageButton buttonScrollDown;

    @Shadow
    @Final
    private static ImageButton buttonScrollUp;

    @Inject(method = "onLayout", at = @At("TAIL"))
    private static void spicedcider$moveTabsRight(CallbackInfo ci) {
        var space = ScreenManager.getActiveCreativeTabScreenSpace();
        var panel = ScreenManager.getTargetCreativeTabPanel();
        if (space == null || panel == null || !RemiRightTabs.active() || !CreativeModeTabGui.leftTabNavigationBar.visible) {
            return;
        }
        SidebarTheme theme = panel.theme;
        int leftX = space.tx - ReliableEmiConfig.verticalTabsWidth - theme.horizontalPadding;
        int rightX = space.tx + space.tw * ScreenManager.ENTRY_SIZE
                + (theme == SidebarTheme.VANILLA && ReliableEmiConfig.isVerticalScrollbarEnabled() ? ScrollbarWidget.WIDTH - theme.horizontalPadding : 0);
        int shift = rightX - leftX + theme.horizontalPadding / 2;
        CreativeModeTabGui.leftTabNavigationBar.pos(rightX, CreativeModeTabGui.leftTabNavigationBar.getY());
        buttonScrollUp.setX(buttonScrollUp.getX() + shift);
        buttonScrollDown.setX(buttonScrollDown.getX() + shift);
    }
}
