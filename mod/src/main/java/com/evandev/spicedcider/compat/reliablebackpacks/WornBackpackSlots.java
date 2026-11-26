package com.evandev.spicedcider.compat.reliablebackpacks;

import com.evandev.spicedcider.mixin.reliablebackpacks.SlotAccessor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class WornBackpackSlots {
    private static final int MAIN_END = 36;
    private static final int HOTBAR_END = 9;

    public static WornBackpackContainer addTo(Player owner, Consumer<Slot> addSlot) {
        WornBackpackContainer backpack = new WornBackpackContainer(owner);
        for (int i = 0; i < WornBackpackContainer.SIZE; i++) {
            addSlot.accept(new WornBackpackSlot(backpack, i));
        }
        return backpack;
    }

    public static int first(AbstractContainerMenu menu) {
        for (int i = menu.slots.size() - 1; i >= 0; i--) {
            if (menu.slots.get(i) instanceof WornBackpackSlot slot && slot.getContainerSlot() == 0) return i;
        }
        return -1;
    }

    public static boolean isBackpackSlot(AbstractContainerMenu menu, int index) {
        return index >= 0 && index < menu.slots.size() && menu.slots.get(index) instanceof WornBackpackSlot;
    }

    public static boolean isOpen(AbstractContainerMenu menu) {
        int first = first(menu);
        return first >= 0 && menu.slots.get(first).isActive();
    }

    public static void layout(AbstractContainerMenu menu, boolean expanded) {
        int offset = expanded ? WornBackpackSlot.ROWS_OFFSET : 0;
        for (Slot slot : menu.slots) {
            if (!isPlayerMain(slot)) continue;
            int index = slot.getContainerSlot();
            int base = index < HOTBAR_END ? 142 : 84 + (index - HOTBAR_END) / 9 * 18;
            ((SlotAccessor) slot).spicedcider$setY(base + offset);
        }
    }

    private static boolean isPlayerMain(Slot slot) {
        return slot.container instanceof Inventory && slot.getContainerSlot() < MAIN_END;
    }

    private static int[] playerRange(AbstractContainerMenu menu) {
        int start = -1;
        int end = -1;
        for (int i = 0; i < menu.slots.size(); i++) {
            if (isPlayerMain(menu.slots.get(i))) {
                if (start < 0) start = i;
                end = i + 1;
            }
        }
        return new int[]{start, end};
    }

    public static ItemStack moveFromBackpack(AbstractContainerMenu menu, Player player, int index, Mover mover) {
        int[] range = playerRange(menu);
        return move(menu, player, index, stack -> range[0] >= 0 && mover.move(stack, range[0], range[1], false));
    }

    public static ItemStack moveIntoPlayer(AbstractContainerMenu menu, Player player, int index, Mover mover) {
        int[] range = playerRange(menu);
        int first = first(menu);
        return move(menu, player, index, stack -> range[0] >= 0 && (mover.move(stack, range[0], range[1], true)
                || first >= 0 && mover.move(stack, first, first + WornBackpackContainer.SIZE, false)));
    }

    private static ItemStack move(AbstractContainerMenu menu, Player player, int index, Predicate<ItemStack> moved) {
        Slot slot = menu.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (!moved.test(stack)) return ItemStack.EMPTY;
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY, copy);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return copy;
    }

    @FunctionalInterface
    public interface Mover {
        boolean move(ItemStack stack, int start, int end, boolean reverse);
    }
}
