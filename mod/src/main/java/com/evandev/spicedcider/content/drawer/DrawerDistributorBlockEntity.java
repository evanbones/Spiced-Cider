package com.evandev.spicedcider.content.drawer;

import com.evandev.spicedcider.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DrawerDistributorBlockEntity extends BlockEntity {

    public static final int RANGE = 50;
    private static final int CACHE_TICKS = 20;
    private static final int DOUBLE_CLICK_TICKS = 10;

    private final List<BlockPos> drawers = new ArrayList<>();
    private long lastScan;
    private boolean dirty = true;
    private long lastClickTime;
    private UUID lastClickPlayer;
    private final IItemHandler itemHandler = new Handler();

    public DrawerDistributorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_DRAWER_DISTRIBUTOR.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public List<ItemDrawerBlockEntity> drawers() {
        if (level == null) return List.of();
        long time = level.getGameTime();
        if (dirty || time - lastScan >= CACHE_TICKS || time < lastScan) {
            scan();
            lastScan = time;
            dirty = false;
        }
        List<ItemDrawerBlockEntity> result = new ArrayList<>(drawers.size());
        for (BlockPos pos : drawers) {
            if (level.isLoaded(pos) && level.getBlockEntity(pos) instanceof ItemDrawerBlockEntity drawer) result.add(drawer);
        }
        return result;
    }

    private void scan() {
        drawers.clear();
        if (level == null) return;
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        queue.add(worldPosition);
        seen.add(worldPosition);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!pos.equals(worldPosition)) {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof ItemDrawerBlock) {
                    drawers.add(pos);
                } else if (!(state.getBlock() instanceof DrawerDistributorBlock)) {
                    continue;
                }
            }
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (seen.contains(next) || !level.isLoaded(next)) continue;
                if (Math.max(Math.max(Math.abs(next.getX() - worldPosition.getX()), Math.abs(next.getY() - worldPosition.getY())), Math.abs(next.getZ() - worldPosition.getZ())) > RANGE) continue;
                seen.add(next);
                queue.add(next);
            }
        }
        drawers.sort(BlockPos::compareTo);
    }

    public int insert(ItemStack stack, boolean simulate, boolean allowEmptyDrawers) {
        if (stack.isEmpty()) return 0;
        List<ItemDrawerBlockEntity> network = drawers();
        int remaining = stack.getCount();
        for (ItemDrawerBlockEntity drawer : network) {
            if (remaining <= 0) break;
            if (drawer.holds(stack)) remaining -= drawer.insert(stack.copyWithCount(remaining), simulate, false);
        }
        if (allowEmptyDrawers) {
            for (ItemDrawerBlockEntity drawer : network) {
                if (remaining <= 0) break;
                if (drawer.getItem().isEmpty()) remaining -= drawer.insert(stack.copyWithCount(remaining), simulate, false);
            }
        }
        return stack.getCount() - remaining;
    }

    public int putFromPlayer(Player player) {
        long time = level == null ? 0 : level.getGameTime();
        boolean doubleClick = time - lastClickTime < DOUBLE_CLICK_TICKS && player.getUUID().equals(lastClickPlayer);
        lastClickTime = time;
        lastClickPlayer = player.getUUID();
        dirty = true;

        int added = 0;
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty()) {
            int moved = insert(held, false, true);
            held.shrink(moved);
            added += moved;
        }
        if (doubleClick) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) continue;
                int moved = insert(stack, false, false);
                stack.shrink(moved);
                added += moved;
            }
        }
        if (added > 0) player.getInventory().setChanged();
        return added;
    }

    private class Handler implements IItemHandler {

        @Override
        public int getSlots() {
            return drawers().size() + 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            ItemDrawerBlockEntity drawer = drawer(slot);
            if (drawer == null || drawer.isEmpty()) return ItemStack.EMPTY;
            return drawer.getItem().copyWithCount(drawer.getCount());
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return ItemStack.EMPTY;
            int added = insert(stack, simulate, true);
            if (added <= 0) return stack;
            return added >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - added);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemDrawerBlockEntity drawer = drawer(slot);
            return drawer == null ? ItemStack.EMPTY : drawer.extract(amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            ItemDrawerBlockEntity drawer = drawer(slot);
            return drawer == null ? Integer.MAX_VALUE : drawer.getCapacity();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }

        private ItemDrawerBlockEntity drawer(int slot) {
            List<ItemDrawerBlockEntity> network = drawers();
            int index = slot - 1;
            return index >= 0 && index < network.size() ? network.get(index) : null;
        }
    }
}
