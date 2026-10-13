package com.evandev.spicedcider.compat.voxyworldgen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
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

public class LodSyncedChunks extends SavedData {
    public static final String DATA_NAME = "spicedcider_voxy_lod_synced";
    public static final SavedData.Factory<LodSyncedChunks> FACTORY = new SavedData.Factory<>(LodSyncedChunks::new, LodSyncedChunks::load, null);

    private final Map<UUID, LongSet> players = new ConcurrentHashMap<>();
    private final Map<UUID, LongSet> live = new ConcurrentHashMap<>();

    public static LodSyncedChunks get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public void attach(UUID player, LongSet synced) {
        LongSet stored = players.get(player);
        if (stored != null && stored != synced) synced.addAll(stored);
        players.put(player, synced);
        live.put(player, synced);
    }

    public void detach(UUID player, LongSet synced) {
        if (live.remove(player, synced)) {
            players.put(player, new LongOpenHashSet(synced.toLongArray()));
            setDirty();
        }
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || !live.isEmpty();
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        CompoundTag list = new CompoundTag();
        players.forEach((uuid, chunks) -> list.put(uuid.toString(), new LongArrayTag(chunks.toLongArray())));
        tag.put("players", list);
        return tag;
    }

    public static LodSyncedChunks load(CompoundTag tag, HolderLookup.Provider registries) {
        LodSyncedChunks data = new LodSyncedChunks();
        CompoundTag list = tag.getCompound("players");
        for (String key : list.getAllKeys()) {
            if (list.getTagType(key) != Tag.TAG_LONG_ARRAY) continue;
            try {
                data.players.put(UUID.fromString(key), new LongOpenHashSet(list.getLongArray(key)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return data;
    }
}
