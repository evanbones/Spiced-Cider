package com.evandev.spicedcider.content.vault;

import com.evandev.spicedcider.mixin.minecraft.SlotPositionAccessor;
import com.evandev.spicedcider.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class VaultMenu extends AbstractContainerMenu {
    public static final int VISIBLE_ROWS = 6;
    public static final int SLOT_X = 8;
    public static final int SLOT_Y = 18;

    private final int vaultSlots;
    private final int rows;
    private final IItemHandler handler;
    private final Predicate<Player> validity;
    public int scrollRow;

    public VaultMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(id, inventory, new ItemStackHandler(buf.readVarInt()), player -> true);
    }

    public VaultMenu(int id, Inventory inventory, IItemHandler handler, Predicate<Player> validity) {
        super(ModMenuTypes.ITEM_VAULT.get(), id);
        this.vaultSlots = handler.getSlots();
        this.handler = handler;
        this.rows = Mth.positiveCeilDiv(this.vaultSlots, 9);
        this.validity = validity;
        for (int i = 0; i < this.vaultSlots; i++) {
            addSlot(new VaultSlot(handler, this, i, SLOT_X + (i % 9) * 18, SLOT_Y + (i / 9) * 18));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 17 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 17 + col * 18, 198));
        }
    }

    public int vaultSlots() {
        return this.vaultSlots;
    }

    public IItemHandler handler() {
        return this.handler;
    }

    public int usedSlots() {
        int used = 0;
        for (int i = 0; i < this.vaultSlots; i++) {
            if (this.slots.get(i).hasItem()) used++;
        }
        return used;
    }

    private int hiddenRows() {
        return Math.max(0, this.rows - VISIBLE_ROWS);
    }

    public boolean canScroll() {
        return hiddenRows() > 0;
    }

    public int rowForScroll(float scroll) {
        return Math.max(0, (int) (scroll * hiddenRows() + 0.5F));
    }

    public float scrollForRow(int row) {
        return canScroll() ? Mth.clamp((float) row / hiddenRows(), 0.0F, 1.0F) : 0.0F;
    }

    public float scrollBy(float scroll, double delta) {
        return canScroll() ? Mth.clamp(scroll - (float) (delta / hiddenRows()), 0.0F, 1.0F) : 0.0F;
    }

    public void scrollTo(float scroll) {
        this.scrollRow = rowForScroll(scroll);
        for (int i = 0; i < this.vaultSlots; i++) {
            if (this.slots.get(i) instanceof VaultSlot slot) {
                ((SlotPositionAccessor) slot).spicedcider$setSlotY(slot.initialY - this.scrollRow * 18);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < this.vaultSlots) {
            if (!moveItemStackTo(stack, this.vaultSlots, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, this.vaultSlots, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.validity.test(player);
    }
}
