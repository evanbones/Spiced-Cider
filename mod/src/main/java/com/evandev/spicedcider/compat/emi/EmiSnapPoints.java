package com.evandev.spicedcider.compat.emi;

import dev.emi.emi.config.SidebarSide;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.gui.components.AbstractWidget;

public class EmiSnapPoints {
    public interface Sink {
        void accept(int x, int y, int range);
    }

    public static void collect(Sink sink) {
        if (EmiScreenManager.isDisabled()) return;
        widget(EmiScreenManager.search, sink);
        widget(EmiScreenManager.emi, sink);
        widget(EmiScreenManager.tree, sink);
        for (SidebarSide side : SidebarSide.values()) {
            EmiScreenManager.SidebarPanel panel = EmiScreenManager.getPanelFor(side);
            if (panel == null || !panel.isVisible()) continue;
            widget(panel.pageLeft, sink);
            widget(panel.pageRight, sink);
            widget(panel.cycle, sink);
            for (EmiScreenManager.ScreenSpace space : panel.getSpaces()) {
                int count = space == panel.space ? space.getPage(panel.page).size() : Math.min(space.pageSize, space.getStacks().size());
                for (int i = 0; i < count; i++) {
                    sink.accept(space.getRawX(i) + 9, space.getRawY(i) + 9, 9);
                }
            }
        }
    }

    private static void widget(AbstractWidget widget, Sink sink) {
        if (widget == null || !widget.visible) return;
        sink.accept(widget.getX() + widget.getWidth() / 2, widget.getY() + widget.getHeight() / 2, Math.min(widget.getWidth(), widget.getHeight()) / 2);
    }
}
