package com.evandev.spicedcider.compat.quark;

import com.evandev.spicedcider.content.vault.VaultMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.violetmoon.quark.base.handler.SortingHandler;

import java.util.ArrayList;
import java.util.List;

public class VaultSorting {
    public static void sort(Player player, VaultMenu menu) {
        IItemHandler handler = menu.handler();
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            while (!handler.getStackInSlot(slot).isEmpty()) {
                ItemStack taken = handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
                if (taken.isEmpty()) break;
                merge(stacks, taken, handler.getSlotLimit(slot));
            }
        }
        SortingHandler.sortStackList(stacks);
        int slot = 0;
        for (ItemStack stack : stacks) {
            ItemStack remaining = stack;
            while (!remaining.isEmpty() && slot < handler.getSlots()) {
                remaining = handler.insertItem(slot, remaining, false);
                if (!remaining.isEmpty()) slot++;
            }
            if (!remaining.isEmpty()) player.getInventory().placeItemBackInInventory(remaining);
        }
        menu.broadcastChanges();
    }

    private static void merge(List<ItemStack> stacks, ItemStack taken, int slotLimit) {
        for (ItemStack existing : stacks) {
            if (taken.isEmpty()) return;
            if (!ItemStack.isSameItemSameComponents(existing, taken)) continue;
            int limit = Math.min(slotLimit, existing.getMaxStackSize());
            int moved = Math.min(taken.getCount(), limit - existing.getCount());
            if (moved <= 0) continue;
            existing.grow(moved);
            taken.shrink(moved);
        }
        if (!taken.isEmpty()) stacks.add(taken);
    }
}
