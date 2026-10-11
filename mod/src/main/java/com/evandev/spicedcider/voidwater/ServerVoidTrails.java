package com.evandev.spicedcider.voidwater;

import com.evandev.spicedcider.networking.VoidTrailPayload;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ServerVoidTrails extends SavedData {
    public static final String DATA_NAME = "spicedcider_void_trails";
    public static final SavedData.Factory<ServerVoidTrails> FACTORY = new SavedData.Factory<>(ServerVoidTrails::new, ServerVoidTrails::load, null);

    private final VoidTrailColumns columns = new VoidTrailColumns();
    private final LongOpenHashSet active = new LongOpenHashSet();
    private final Long2IntOpenHashMap growth = new Long2IntOpenHashMap();

    public VoidTrailColumns columns() {
        return columns;
    }

    public void activate(int x, int z) {
        active.add(VoidTrails.pack(x, z));
    }

    public void tick(ServerLevel level) {
        if (active.isEmpty()) return;

        int minY = level.getMinBuildHeight();
        int max = VoidTrails.maxLength();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        LongArrayList changedColumns = new LongArrayList();
        ByteArrayList changedLengths = new ByteArrayList();

        LongIterator iterator = active.iterator();
        while (iterator.hasNext()) {
            long column = iterator.nextLong();
            int x = VoidTrails.unpackX(column);
            int z = VoidTrails.unpackZ(column);
            if (!level.hasChunk(x >> 4, z >> 4)) continue;

            FluidState fluid = level.getFluidState(pos.set(x, minY, z));
            int length = columns.get(x, z);
            int next;
            boolean settled;
            if (fluid.isEmpty()) {
                next = 0;
                settled = true;
            } else if (length >= max) {
                next = max;
                settled = true;
            } else if (growth.addTo(column, 1) + 1 >= fluid.getType().getTickDelay(level)) {
                growth.remove(column);
                next = length + 1;
                settled = next >= max;
            } else {
                continue;
            }

            if (settled) {
                growth.remove(column);
                iterator.remove();
            }
            if (next != length && columns.set(x, z, next)) {
                changedColumns.add(column);
                changedLengths.add((byte) next);
            }
        }

        if (!changedColumns.isEmpty()) {
            setDirty();
            PacketDistributor.sendToPlayersInDimension(level, new VoidTrailPayload(changedColumns.toLongArray(), changedLengths.toByteArray()));
        }
    }

    public void watch(ServerPlayer player, LevelChunk chunk, ServerLevel level) {
        ChunkPos chunkPos = chunk.getPos();
        int minY = level.getMinBuildHeight();
        int baseX = chunkPos.getMinBlockX();
        int baseZ = chunkPos.getMinBlockZ();

        LevelChunkSection bottom = chunk.getSection(0);
        if (!bottom.hasOnlyAir() && bottom.maybeHas(state -> !state.getFluidState().isEmpty())) {
            for (int i = 0; i < 256; i++) {
                int x = baseX + (i & 15);
                int z = baseZ + (i >> 4);
                if (!chunk.getFluidState(x, minY, z).isEmpty()) {
                    active.add(VoidTrails.pack(x, z));
                }
            }
        }

        byte[] lengths = columns.chunk(chunkPos.toLong());
        if (lengths == null) return;

        LongArrayList sentColumns = new LongArrayList();
        ByteArrayList sentLengths = new ByteArrayList();
        for (int i = 0; i < 256; i++) {
            if (lengths[i] == 0) continue;
            long column = VoidTrails.pack(baseX + (i & 15), baseZ + (i >> 4));
            active.add(column);
            sentColumns.add(column);
            sentLengths.add(lengths[i]);
        }
        if (!sentColumns.isEmpty()) {
            PacketDistributor.sendToPlayer(player, new VoidTrailPayload(sentColumns.toLongArray(), sentLengths.toByteArray()));
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag chunks = new ListTag();
        for (Map.Entry<Long, byte[]> entry : columns.chunks().entrySet()) {
            CompoundTag chunk = new CompoundTag();
            chunk.putLong("pos", entry.getKey());
            chunk.putByteArray("lengths", entry.getValue().clone());
            chunks.add(chunk);
        }
        tag.put("chunks", chunks);
        return tag;
    }

    private static ServerVoidTrails load(CompoundTag tag, HolderLookup.Provider registries) {
        ServerVoidTrails trails = new ServerVoidTrails();
        for (Tag element : tag.getList("chunks", Tag.TAG_COMPOUND)) {
            CompoundTag chunk = (CompoundTag) element;
            byte[] lengths = chunk.getByteArray("lengths");
            if (lengths.length == 256) {
                trails.columns.putChunk(chunk.getLong("pos"), lengths);
            }
        }
        return trails;
    }
}
