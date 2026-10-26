package com.evandev.spicedcider.compat.reliablebackpacks;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.Nullable;

public class WornBackpackContainer implements Container {
    public static final int SIZE = 27;

    private final Player owner;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private ItemStack source = ItemStack.EMPTY;
    @Nullable
    private ItemContainerContents loaded;

    public WornBackpackContainer(Player owner) {
        this.owner = owner;
    }

    public static boolean hasContents(ItemStack stack) {
        if (!stack.is(BPItems.BACKPACK)) return false;
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        return contents != null && contents.nonEmptyItems().iterator().hasNext();
    }

    public ItemStack backpack() {
        ItemStack chest = this.owner.getItemBySlot(EquipmentSlot.CHEST);
        return chest.is(BPItems.BACKPACK) ? chest : ItemStack.EMPTY;
    }

    public boolean isOpen() {
        return !this.backpack().isEmpty();
    }

    private void sync() {
        ItemStack stack = this.backpack();
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (stack != this.source || contents != this.loaded) {
            this.source = stack;
            this.loaded = contents;
            this.items.clear();
            contents.copyInto(this.items);
        }
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        this.sync();
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        this.sync();
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.sync();
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);
        if (!removed.isEmpty()) this.setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        this.sync();
        ItemStack removed = ContainerHelper.takeItem(this.items, slot);
        if (!removed.isEmpty()) this.setChanged();
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.sync();
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        this.setChanged();
    }

    @Override
    public void setChanged() {
        ItemStack stack = this.backpack();
        if (stack.isEmpty() || stack != this.source) return;
        this.loaded = ItemContainerContents.fromItems(this.items);
        stack.set(DataComponents.CONTAINER, this.loaded);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.sync();
        this.items.clear();
        this.setChanged();
    }
}
