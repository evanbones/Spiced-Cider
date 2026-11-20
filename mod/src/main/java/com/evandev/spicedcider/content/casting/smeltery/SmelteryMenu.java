package com.evandev.spicedcider.content.casting.smeltery;

import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class SmelteryMenu extends AbstractContainerMenu {
    public static final int SLOT_X = 30;
    public static final int SLOT_Y = 18;
    public static final int SLOT_STEP_X = 23;
    public static final int SLOT_STEP_Y = 32;
    public static final int DUMP_OFFSET = 100;

    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final BlockPos pos;
    @Nullable
    private final SmelteryBlockEntity smeltery;

    public SmelteryMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(id, inventory, buf.readBlockPos());
    }

    private SmelteryMenu(int id, Inventory inventory, BlockPos pos) {
        this(id, inventory, inventory.player.level().getBlockEntity(pos) instanceof SmelteryBlockEntity be ? be : null,
                new ItemStackHandler(SmelteryBlockEntity.INPUTS), new SimpleContainerData(SmelteryBlockEntity.DATA_COUNT), pos);
    }

    public SmelteryMenu(int id, Inventory inventory, SmelteryBlockEntity smeltery, ContainerData data) {
        this(id, inventory, smeltery, smeltery.getItems(), data, smeltery.getBlockPos());
    }

    private SmelteryMenu(int id, Inventory inventory, @Nullable SmelteryBlockEntity smeltery, IItemHandler items, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.SMELTERY.get(), id);
        this.smeltery = smeltery;
        this.data = data;
        this.pos = pos;
        this.access = ContainerLevelAccess.create(inventory.player.level(), pos);
        IItemHandler handler = smeltery != null && !inventory.player.level().isClientSide() ? smeltery.getItems() : items;
        for (int i = 0; i < SmelteryBlockEntity.INPUTS; i++) {
            addSlot(new SlotItemHandler(handler, i, slotX(i), slotY(i)));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
        addDataSlots(data);
    }

    public static int slotX(int slot) {
        return SLOT_X + (slot % 4) * SLOT_STEP_X;
    }

    public static int slotY(int slot) {
        return SLOT_Y + (slot / 4) * SLOT_STEP_Y;
    }

    @Nullable
    public SmelteryBlockEntity smeltery() {
        return this.smeltery;
    }

    public BlockPos pos() {
        return this.pos;
    }

    public float progress(int slot) {
        int total = this.data.get(SmelteryBlockEntity.INPUTS + slot);
        return total <= 0 ? 0.0F : Math.min(1.0F, (float) this.data.get(slot) / total);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.smeltery != null) {
            if (id >= DUMP_OFFSET) {
                this.smeltery.dumpMetal(id - DUMP_OFFSET);
            } else {
                this.smeltery.selectMetal(id);
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int machine = SmelteryBlockEntity.INPUTS;
        if (index < machine) {
            if (!moveItemStackTo(stack, machine, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, machine, false)) {
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
        return stillValid(this.access, player, ModBlocks.SMELTERY.get());
    }
}
