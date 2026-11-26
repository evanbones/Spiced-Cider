package com.evandev.spicedcider.content.drawer;

import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ItemDrawerBlockEntity extends BlockEntity {

    public static final int STACKS = 80;
    private static final int DOUBLE_CLICK_TICKS = 10;

    private ItemStack item = ItemStack.EMPTY;
    private int count;
    private boolean locked;
    private long lastClickTime;
    private UUID lastClickPlayer;
    private final IItemHandler itemHandler = new Handler();

    public ItemDrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_DRAWER.get(), pos, state);
    }

    public ItemStack getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isEmpty() {
        return count <= 0;
    }

    public int getCapacity() {
        return STACKS * (item.isEmpty() ? 64 : item.getMaxStackSize());
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public boolean holds(ItemStack stack) {
        return !item.isEmpty() && ItemStack.isSameItemSameComponents(item, stack);
    }

    public boolean canAccept(ItemStack stack, boolean manual) {
        if (stack.isEmpty() || stack.has(ModDataComponents.DRAWER_CONTENTS.get())) return false;
        if (item.isEmpty()) return manual || !locked;
        return ItemStack.isSameItemSameComponents(item, stack);
    }

    public int insert(ItemStack stack, boolean simulate, boolean manual) {
        if (!canAccept(stack, manual)) return 0;
        int capacity = STACKS * stack.getMaxStackSize();
        int added = Math.min(stack.getCount(), capacity - count);
        if (added <= 0 || simulate) return Math.max(added, 0);
        if (item.isEmpty()) item = stack.copyWithCount(1);
        count += added;
        changed();
        return added;
    }

    public ItemStack extract(int amount, boolean simulate) {
        if (item.isEmpty() || count <= 0 || amount <= 0) return ItemStack.EMPTY;
        int taken = Math.min(amount, count);
        ItemStack result = item.copyWithCount(taken);
        if (simulate) return result;
        count -= taken;
        if (count <= 0) {
            count = 0;
            if (!locked) item = ItemStack.EMPTY;
        }
        changed();
        return result;
    }

    public int putFromPlayer(Player player) {
        long time = level == null ? 0 : level.getGameTime();
        boolean doubleClick = time - lastClickTime < DOUBLE_CLICK_TICKS && player.getUUID().equals(lastClickPlayer);
        lastClickTime = time;
        lastClickPlayer = player.getUUID();

        int added = 0;
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty()) {
            int moved = insert(held, false, true);
            held.shrink(moved);
            added += moved;
        }
        if (doubleClick && !item.isEmpty()) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty() || !holds(stack)) continue;
                int moved = insert(stack, false, true);
                stack.shrink(moved);
                added += moved;
            }
        }
        if (added > 0) player.getInventory().setChanged();
        return added;
    }

    public void toggleLock() {
        locked = !locked;
        if (!locked && count <= 0) item = ItemStack.EMPTY;
        if (level != null && getBlockState().hasProperty(ItemDrawerBlock.LOCKED)) {
            level.setBlock(worldPosition, getBlockState().setValue(ItemDrawerBlock.LOCKED, locked), 3);
        }
        changed();
    }

    public int getComparatorSignal() {
        if (count <= 0) return 0;
        return 1 + (int) Math.floor(14.0 * count / getCapacity());
    }

    private void changed() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
            level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
        }
    }

    private void read(DrawerContents contents) {
        item = contents.item().isEmpty() ? ItemStack.EMPTY : contents.item().copyWithCount(1);
        count = item.isEmpty() ? 0 : Math.max(contents.count(), 0);
        locked = contents.locked();
    }

    private DrawerContents contents() {
        return new DrawerContents(item.copy(), count, locked);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!item.isEmpty()) tag.put("Item", item.save(registries));
        tag.putInt("Count", count);
        tag.putBoolean("Locked", locked);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        item = tag.contains("Item") ? ItemStack.parseOptional(registries, tag.getCompound("Item")) : ItemStack.EMPTY;
        count = item.isEmpty() ? 0 : tag.getInt("Count");
        locked = tag.getBoolean("Locked");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        input.get(DataComponents.MAX_STACK_SIZE);
        DrawerContents contents = input.get(ModDataComponents.DRAWER_CONTENTS.get());
        if (contents != null) read(contents);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (count > 0 || locked) builder.set(ModDataComponents.DRAWER_CONTENTS.get(), contents());
        if (count > 0) builder.set(DataComponents.MAX_STACK_SIZE, 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("Item");
        tag.remove("Count");
        tag.remove("Locked");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private class Handler implements IItemHandler {

        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot != 1 || item.isEmpty() || count <= 0) return ItemStack.EMPTY;
            return item.copyWithCount(count);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;
            int added = insert(stack, simulate, false);
            if (added <= 0) return stack;
            return added >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - added);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 1) return ItemStack.EMPTY;
            return extract(amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? Integer.MAX_VALUE : getCapacity();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return canAccept(stack, false);
        }
    }
}
