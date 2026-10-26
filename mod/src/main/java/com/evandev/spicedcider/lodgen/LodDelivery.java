package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.compat.voxy.VoxyIngest;
import com.evandev.spicedcider.networking.LodChunkPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

final class LodDelivery {
    private static final int MAX_QUEUED = 256;
    private static final int MAX_VOXY_BACKLOG = 4096;

    private final Map<UUID, Outgoing> outgoing = new ConcurrentHashMap<>();

    boolean accepting(LodTarget target) {
        if (target.host()) {
            int backlog = VoxyIngest.backlog();
            return backlog >= 0 && backlog < MAX_VOXY_BACKLOG;
        }
        Outgoing queue = outgoing.get(target.player());
        return queue == null || queue.size.get() < MAX_QUEUED;
    }

    int queued(UUID player) {
        Outgoing queue = outgoing.get(player);
        return queue == null ? 0 : queue.size.get();
    }

    void deliver(ServerLevel level, LodColumn column, List<LodTarget> recipients) {
        long pos = ChunkPos.asLong(column.x(), column.z());
        LodChunkPayload payload = null;
        for (LodTarget target : recipients) {
            if (target.host()) continue;
            if (payload == null) payload = new LodChunkPayload(level.dimension(), column.x(), column.z(), column.encode());
            Outgoing queue = outgoing.computeIfAbsent(target.player(), uuid -> new Outgoing());
            queue.payloads.add(payload);
            queue.size.incrementAndGet();
            target.done().add(pos);
        }
        for (LodTarget target : recipients) {
            if (target.host() && VoxyIngest.ingest(level, column)) target.done().add(pos);
        }
    }

    void tick(MinecraftServer server, int chunksPerSecond) {
        Iterator<Map.Entry<UUID, Outgoing>> iterator = outgoing.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Outgoing> entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                drop(server, entry.getKey(), entry.getValue());
                continue;
            }
            Outgoing queue = entry.getValue();
            queue.budget = Math.min(queue.budget + chunksPerSecond / 20.0, Math.max(1.0, chunksPerSecond / 20.0) * 2);
            while (queue.budget >= 1.0) {
                LodChunkPayload payload = queue.payloads.poll();
                if (payload == null) break;
                queue.size.decrementAndGet();
                if (player.level().dimension() != payload.dimension()) {
                    unmark(server, player.getUUID(), payload);
                    continue;
                }
                PacketDistributor.sendToPlayer(player, payload);
                queue.budget -= 1.0;
            }
        }
    }

    void forget(MinecraftServer server, UUID player) {
        Outgoing queue = outgoing.remove(player);
        if (queue != null) drop(server, player, queue);
    }

    private static void drop(MinecraftServer server, UUID player, Outgoing queue) {
        LodChunkPayload payload;
        while ((payload = queue.payloads.poll()) != null) unmark(server, player, payload);
    }

    private static void unmark(MinecraftServer server, UUID player, LodChunkPayload payload) {
        ServerLevel level = server.getLevel(payload.dimension());
        if (level != null) LodLedger.get(level).chunks(player).remove(ChunkPos.asLong(payload.x(), payload.z()));
    }

    void clear(MinecraftServer server) {
        outgoing.forEach((player, queue) -> drop(server, player, queue));
        outgoing.clear();
    }

    private static final class Outgoing {
        final Queue<LodChunkPayload> payloads = new ConcurrentLinkedQueue<>();
        final AtomicInteger size = new AtomicInteger();
        double budget;
    }
}
