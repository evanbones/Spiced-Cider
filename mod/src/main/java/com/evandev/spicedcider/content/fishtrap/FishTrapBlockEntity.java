package com.evandev.spicedcider.content.fishtrap;

import com.evandev.spicedcider.compat.tide.TideCompat;
import com.evandev.spicedcider.registry.ModBlockEntities;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class FishTrapBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int BAIT_SLOT = 0;
    public static final int CATCH_SLOTS = 9;
    public static final int SIZE = CATCH_SLOTS + 1;
    private static final int MIN_INTERVAL = 2400;
    private static final int MAX_INTERVAL = 4800;
    private static final int WATER_RADIUS = 2;
    private static final int WATER_HEIGHT = 3;
    private static final int[] BAIT_SLOTS = {BAIT_SLOT};
    private static final int[] OUTPUT_SLOTS = IntStream.rangeClosed(1, CATCH_SLOTS).toArray();

    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int cooldown = -1;

    public FishTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISH_TRAP.get(), pos, state);
    }

    public static boolean isBait(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(ModTags.Items.FISH_TRAP_BAIT) || TideCompat.isBait(stack));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FishTrapBlockEntity trap) {
        RandomSource random = level.getRandom();
        if (trap.cooldown < 0) trap.cooldown = interval(random);
        if (trap.cooldown-- > 0) return;
        trap.cooldown = interval(random);
        if (level instanceof ServerLevel serverLevel && state.getValue(FishTrapBlock.WATERLOGGED)) {
            trap.tryCatch(serverLevel, pos, random);
        }
    }

    private static int interval(RandomSource random) {
        return random.nextIntBetweenInclusive(MIN_INTERVAL, MAX_INTERVAL);
    }

    private void tryCatch(ServerLevel level, BlockPos pos, RandomSource random) {
        ItemStack bait = this.items.get(BAIT_SLOT);
        if (!isBait(bait) || !hasRoom()) return;
        if (random.nextFloat() >= waterFraction(level, pos)) return;

        int luck = TideCompat.baitLuck(bait);
        int strength = Math.max(1, luck + TideCompat.baitSpeed(bait));
        int fish = 1 + random.nextInt(strength);
        boolean caught = false;
        for (int i = 0; i < fish; i++) {
            for (ItemStack stack : roll(level, pos, luck)) {
                if (stack.isEmpty()) continue;
                ItemStack copy = stack.copy();
                TideCompat.stripBucketableMarker(copy);
                caught |= insert(copy);
            }
        }
        if (!caught) return;
        bait.shrink(1);
        level.playSound(null, pos, SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.BLOCKS, 0.4F, 0.8F + random.nextFloat() * 0.4F);
        setChanged();
    }

    private static List<ItemStack> roll(ServerLevel level, BlockPos pos, int luck) {
        if (TideCompat.isLoaded()) return TideCompat.catchFish(level, pos, luck);
        LootTable table = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING_FISH);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                .withParameter(LootContextParams.TOOL, new ItemStack(level.getBlockState(pos).getBlock()))
                .withLuck(luck)
                .create(LootContextParamSets.FISHING);
        return table.getRandomItems(params);
    }

    private static float waterFraction(Level level, BlockPos pos) {
        int water = 0;
        int total = 0;
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-WATER_RADIUS, 0, -WATER_RADIUS), pos.offset(WATER_RADIUS, WATER_HEIGHT - 1, WATER_RADIUS))) {
            if (p.equals(pos)) continue;
            total++;
            if (level.getFluidState(p).isSourceOfType(Fluids.WATER)) water++;
        }
        return (float) water / total;
    }

    private boolean hasRoom() {
        for (int slot : OUTPUT_SLOTS) {
            if (this.items.get(slot).isEmpty()) return true;
        }
        return false;
    }

    private boolean insert(ItemStack stack) {
        int start = stack.getCount();
        for (int slot : OUTPUT_SLOTS) {
            ItemStack existing = this.items.get(slot);
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
                int moved = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
                existing.grow(moved);
                stack.shrink(moved);
                if (stack.isEmpty()) return true;
            }
        }
        for (int slot : OUTPUT_SLOTS) {
            if (this.items.get(slot).isEmpty()) {
                this.items.set(slot, stack.copy());
                stack.setCount(0);
                return true;
            }
        }
        return stack.getCount() < start;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("Cooldown", this.cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.cooldown = tag.contains("Cooldown") ? tag.getInt("Cooldown") : -1;
    }

    @Override
    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new FishTrapMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == BAIT_SLOT && isBait(stack);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? OUTPUT_SLOTS : BAIT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return side == Direction.DOWN && slot != BAIT_SLOT;
    }
}
