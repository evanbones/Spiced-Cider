package com.evandev.spicedcider.lodgen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LodLedger extends SavedData {
    public static final String DATA_NAME = "spicedcider_voxy_lod_synced";
    public static final SavedData.Factory<LodLedger> FACTORY = new SavedData.Factory<>(LodLedger::new, LodLedger::load, null);

    private final Map<UUID, LongSet> players = new ConcurrentHashMap<>();

    public static LodLedger get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public LongSet chunks(UUID player) {
        return players.computeIfAbsent(player, uuid -> LongSets.synchronize(new LongOpenHashSet()));
    }

    public void clear(UUID player) {
        LongSet chunks = players.get(player);
        if (chunks != null) chunks.clear();
        setDirty();
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || !players.isEmpty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        CompoundTag list = new CompoundTag();
        players.forEach((uuid, chunks) -> {
            long[] array;
            synchronized (chunks) {
                array = chunks.toLongArray();
            }
            list.put(uuid.toString(), new LongArrayTag(array));
        });
        tag.put("players", list);
        return tag;
    }

    public static LodLedger load(CompoundTag tag, HolderLookup.Provider registries) {
        LodLedger data = new LodLedger();
        CompoundTag list = tag.getCompound("players");
        for (String key : list.getAllKeys()) {
            if (list.getTagType(key) != Tag.TAG_LONG_ARRAY) continue;
            try {
                data.players.put(UUID.fromString(key), LongSets.synchronize(new LongOpenHashSet(list.getLongArray(key))));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return data;
    }
}
