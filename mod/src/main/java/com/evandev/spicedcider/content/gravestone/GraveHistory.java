package com.evandev.spicedcider.content.gravestone;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import com.evandev.spicedcider.networking.GravePositionsPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GraveHistory extends SavedData {
    private static final String NAME = "spicedcider_graves";

    private final Map<UUID, List<GlobalPos>> graves = new HashMap<>();
    private MinecraftServer server;

    public static GraveHistory get(MinecraftServer server) {
        GraveHistory history = server.overworld().getDataStorage().computeIfAbsent(new Factory<>(GraveHistory::new, GraveHistory::load, null), NAME);
        history.server = server;
        return history;
    }

    public void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new GravePositionsPayload(positions(player.getUUID())));
    }

    private void sync(UUID owner) {
        if (this.server == null) return;
        ServerPlayer player = this.server.getPlayerList().getPlayer(owner);
        if (player != null) sync(player);
    }

    private static GraveHistory load(CompoundTag tag, HolderLookup.Provider registries) {
        GraveHistory history = new GraveHistory();
        for (String key : tag.getAllKeys()) {
            UUID id;
            try {
                id = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                continue;
            }
            List<GlobalPos> positions = new ArrayList<>(GlobalPos.CODEC.listOf().parse(NbtOps.INSTANCE, tag.get(key)).result().orElse(List.of()));
            if (!positions.isEmpty()) history.graves.put(id, positions);
        }
        return history;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        this.graves.forEach((id, positions) -> {
            Tag encoded = GlobalPos.CODEC.listOf().encodeStart(NbtOps.INSTANCE, positions).result().orElse(null);
            if (encoded != null) tag.put(id.toString(), encoded);
        });
        return tag;
    }

    public List<GlobalPos> positions(UUID owner) {
        return List.copyOf(this.graves.getOrDefault(owner, List.of()));
    }

    public void add(UUID owner, GlobalPos pos) {
        List<GlobalPos> positions = this.graves.computeIfAbsent(owner, id -> new ArrayList<>());
        if (!positions.contains(pos)) positions.add(pos);
        setDirty();
        sync(owner);
    }

    public void remove(UUID owner, GlobalPos pos) {
        List<GlobalPos> positions = this.graves.get(owner);
        if (positions != null && positions.remove(pos)) {
            if (positions.isEmpty()) this.graves.remove(owner);
            setDirty();
            sync(owner);
        }
    }
}
