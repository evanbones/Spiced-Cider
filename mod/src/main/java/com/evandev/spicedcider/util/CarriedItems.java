package com.evandev.spicedcider.util;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CarriedItems {
    private static final int MAX_DEPTH = 3;
    private static final Map<ServerPlayer, Map<ItemStack, Lent>> LENT = new WeakHashMap<>();

    private record Lent(ItemStack original, int slot, List<ItemStack> holders) {
    }

    public static boolean has(Player player, Predicate<ItemStack> filter) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (contains(inventory.getItem(i), filter)) return true;
        }
        return false;
    }

    public static boolean contains(ItemStack stack, Predicate<ItemStack> filter) {
        return contains(stack, filter, 0);
    }

    private static boolean contains(ItemStack stack, Predicate<ItemStack> filter, int depth) {
        if (stack.isEmpty()) return false;
        if (filter.test(stack)) return true;
        if (depth >= MAX_DEPTH) return false;
        for (ItemStack inner : inner(stack)) {
            if (contains(inner, filter, depth + 1)) return true;
        }
        return false;
    }

    public static void forEachStored(Player player, Consumer<ItemStack> action) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            forEachStored(inventory.getItem(i), action, 0);
        }
    }

    private static void forEachStored(ItemStack stack, Consumer<ItemStack> action, int depth) {
        if (stack.isEmpty() || depth >= MAX_DEPTH) return;
        for (ItemStack inner : inner(stack)) {
            action.accept(inner);
            forEachStored(inner, action, depth + 1);
        }
    }

    public static ItemStack findStored(Player player, Predicate<ItemStack> filter) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            List<ItemStack> path = new ArrayList<>();
            ItemStack found = findStored(inventory.getItem(i), filter, path, 0);
            if (found.isEmpty()) continue;
            if (player instanceof ServerPlayer serverPlayer) {
                int slot = i;
                LENT.computeIfAbsent(serverPlayer, p -> new IdentityHashMap<>())
                        .computeIfAbsent(found, stack -> new Lent(stack.copy(), slot, path));
            }
            return found;
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack findStored(ItemStack holder, Predicate<ItemStack> filter, List<ItemStack> path, int depth) {
        if (holder.isEmpty() || depth >= MAX_DEPTH) return ItemStack.EMPTY;
        path.add(holder);
        for (ItemStack inner : inner(holder)) {
            if (filter.test(inner)) return inner;
            ItemStack found = findStored(inner, filter, path, depth + 1);
            if (!found.isEmpty()) return found;
        }
        path.removeLast();
        return ItemStack.EMPTY;
    }

    public static void writeBackLent(ServerPlayer player) {
        Map<ItemStack, Lent> lent = LENT.remove(player);
        if (lent == null) return;
        Set<Integer> dirtySlots = new HashSet<>();
        for (Map.Entry<ItemStack, Lent> entry : lent.entrySet()) {
            Lent info = entry.getValue();
            if (ItemStack.matches(entry.getKey(), info.original())) continue;
            List<ItemStack> holders = info.holders();
            for (int i = holders.size() - 1; i >= 0; i--) {
                rebuild(holders.get(i));
            }
            dirtySlots.add(info.slot());
        }
        for (int slot : dirtySlots) {
            sync(player, slot);
        }
    }

    public static boolean removeOne(ItemStack holder, Predicate<ItemStack> filter) {
        return removeOne(holder, filter, 0);
    }

    private static boolean removeOne(ItemStack holder, Predicate<ItemStack> filter, int depth) {
        if (holder.isEmpty() || depth >= MAX_DEPTH) return false;
        BundleContents bundle = holder.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) {
            List<ItemStack> items = copies(bundle);
            if (removeFrom(items, filter, depth)) {
                items.removeIf(ItemStack::isEmpty);
                holder.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
                return true;
            }
        }
        ItemContainerContents container = holder.get(DataComponents.CONTAINER);
        if (container != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(container.getSlots(), ItemStack.EMPTY);
            container.copyInto(items);
            if (removeFrom(items, filter, depth)) {
                holder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
                return true;
            }
        }
        return false;
    }

    private static boolean removeFrom(List<ItemStack> items, Predicate<ItemStack> filter, int depth) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack inner = items.get(i);
            if (inner.isEmpty()) continue;
            if (filter.test(inner)) {
                inner.shrink(1);
                if (inner.isEmpty()) items.set(i, ItemStack.EMPTY);
                return true;
            }
            if (removeOne(inner, filter, depth + 1)) return true;
        }
        return false;
    }

    private static List<ItemStack> copies(BundleContents bundle) {
        List<ItemStack> items = new ArrayList<>();
        bundle.itemsCopy().forEach(items::add);
        return items;
    }

    private static Iterable<ItemStack> inner(ItemStack stack) {
        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) return bundle.items();
        ItemContainerContents container = stack.get(DataComponents.CONTAINER);
        if (container != null) return container.nonEmptyItems();
        return List.of();
    }

    private static void rebuild(ItemStack holder) {
        BundleContents bundle = holder.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle != null) {
            holder.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(copies(bundle)));
            return;
        }
        ItemContainerContents container = holder.get(DataComponents.CONTAINER);
        if (container != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(container.getSlots(), ItemStack.EMPTY);
            container.copyInto(items);
            holder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
    }

    private static void sync(ServerPlayer player, int inventorySlot) {
        InventoryMenu menu = player.inventoryMenu;
        for (Slot slot : menu.slots) {
            if (slot.container == player.getInventory() && slot.getContainerSlot() == inventorySlot) {
                player.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), slot.index, slot.getItem().copy()));
                return;
            }
        }
    }
}
