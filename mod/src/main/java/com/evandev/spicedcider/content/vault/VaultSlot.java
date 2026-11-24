package com.evandev.spicedcider.content.vault;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class VaultSlot extends SlotItemHandler {
    public final int initialY;
    private final VaultMenu menu;
    private final int row;

    public VaultSlot(IItemHandler handler, VaultMenu menu, int index, int x, int y) {
        super(handler, index, x, y);
        this.menu = menu;
        this.initialY = y;
        this.row = index / 9;
    }

    @Override
    public boolean isActive() {
        int visibleRow = this.row - this.menu.scrollRow;
        return visibleRow >= 0 && visibleRow < VaultMenu.VISIBLE_ROWS;
    }
}
