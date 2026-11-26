package com.evandev.spicedcider.compat.create;

import com.evandev.spicedcider.content.casting.BasinCastState;
import com.evandev.spicedcider.registry.ModAttachments;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.WeakHashMap;

public class BasinCastSync {
    private static final String KEY = "SpicedCiderCast";
    private static final String DONE_KEY = "SpicedCiderCastDone";
    private static final Map<BlockEntity, Long> RECEIVED = new WeakHashMap<>();
    private static final Map<BlockEntity, ItemStack> DONE = new WeakHashMap<>();

    public static void write(BasinBlockEntity basin, CompoundTag tag, HolderLookup.Provider registries) {
        ItemStack finished = basin.getOutputInventory().getStackInSlot(0);
        if (basin.getLevel() != null && BasinCasting.isCastResult(basin.getLevel(), finished)) {
            tag.put(DONE_KEY, finished.copyWithCount(1).save(registries));
        }
        BasinCastState state = basin.getData(ModAttachments.BASIN_CAST);
        if (state.isEmpty()) return;
        BasinCastState.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), state)
                .result().ifPresent(encoded -> tag.put(KEY, encoded));
    }

    public static void read(BasinBlockEntity basin, CompoundTag tag, HolderLookup.Provider registries) {
        BasinCastState state = tag.contains(KEY)
                ? BasinCastState.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(KEY)).result().orElse(BasinCastState.EMPTY)
                : BasinCastState.EMPTY;
        basin.setData(ModAttachments.BASIN_CAST, state);
        if (basin.getLevel() != null) RECEIVED.put(basin, basin.getLevel().getGameTime());
        ItemStack finished = tag.contains(DONE_KEY) ? ItemStack.parseOptional(registries, tag.getCompound(DONE_KEY)) : ItemStack.EMPTY;
        if (finished.isEmpty()) DONE.remove(basin);
        else DONE.put(basin, finished);
    }

    public static ItemStack finished(BasinBlockEntity basin) {
        return DONE.getOrDefault(basin, ItemStack.EMPTY);
    }

    public static float coolingTicks(BasinBlockEntity basin, BasinCastState state, float partialTick) {
        Long received = RECEIVED.get(basin);
        if (received == null || basin.getLevel() == null) return state.cooling();
        return state.cooling() + (basin.getLevel().getGameTime() - received) + partialTick;
    }
}
