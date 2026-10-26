package com.evandev.spicedcider.compat.reliablebackpacks;

import com.evandev.spicedcider.mixin.reliablebackpacks.SlotAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WornBackpackSlot extends Slot {
    public static final int FIRST = InventoryMenu.SHIELD_SLOT + 1;
    public static final int END = FIRST + WornBackpackContainer.SIZE;
    public static final int EXPANDED_HEIGHT = 224;
    public static final int ROWS_OFFSET = 58;
    public static final int SCREEN_SHIFT = (EXPANDED_HEIGHT - 166) / 2;

    private final WornBackpackContainer backpack;

    public WornBackpackSlot(WornBackpackContainer backpack, int index) {
        super(backpack, index, 8 + index % 9 * 18, 84 + index / 9 * 18);
        this.backpack = backpack;
    }

    public static boolean isOpen(InventoryMenu menu) {
        return menu.slots.size() > FIRST && menu.slots.get(FIRST) instanceof WornBackpackSlot slot && slot.isActive();
    }

    public static void layout(InventoryMenu menu, boolean expanded) {
        int offset = expanded ? ROWS_OFFSET : 0;
        for (int i = InventoryMenu.INV_SLOT_START; i < InventoryMenu.USE_ROW_SLOT_END; i++) {
            int base = i < InventoryMenu.INV_SLOT_END ? 84 + (i - InventoryMenu.INV_SLOT_START) / 9 * 18 : 142;
            ((SlotAccessor) menu.slots.get(i)).spicedcider$setY(base + offset);
        }
    }

    @Override
    public boolean isActive() {
        return this.backpack.isOpen();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.isActive();
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.isActive() && super.mayPickup(player);
    }
}
