package com.evandev.spicedcider.content.fishtrap;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModCriteria;
import com.evandev.spicedcider.registry.ModMenuTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FishTrapMenu extends AbstractContainerMenu {
    private static final ResourceLocation EMPTY_BAIT_SLOT = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "item/empty_slot_bait");

    private final Container container;

    public FishTrapMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(FishTrapBlockEntity.SIZE));
    }

    public FishTrapMenu(int id, Inventory inventory, Container container) {
        super(ModMenuTypes.FISH_TRAP.get(), id);
        checkContainerSize(container, FishTrapBlockEntity.SIZE);
        this.container = container;
        container.startOpen(inventory.player);
        addSlot(new BaitSlot(container, FishTrapBlockEntity.BAIT_SLOT, 81, 15));
        for (int i = 0; i < FishTrapBlockEntity.CATCH_SLOTS; i++) {
            addSlot(new CatchSlot(container, i + 1, 8 + i * 18, 48));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int machine = FishTrapBlockEntity.SIZE;
        if (index < machine) {
            if (!moveItemStackTo(stack, machine, this.slots.size(), true)) return ItemStack.EMPTY;
        } else if (!FishTrapBlockEntity.isBait(stack) || !moveItemStackTo(stack, FishTrapBlockEntity.BAIT_SLOT, FishTrapBlockEntity.BAIT_SLOT + 1, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private static class BaitSlot extends Slot {
        BaitSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return FishTrapBlockEntity.isBait(stack);
        }

        @Override
        public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_BAIT_SLOT);
        }
    }

    private static class CatchSlot extends Slot {
        CatchSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if (player instanceof ServerPlayer serverPlayer) {
                ModCriteria.FISH_TRAP_CATCH.get().trigger(serverPlayer);
            }
            super.onTake(player, stack);
        }
    }
}
