package com.evandev.spicedcider.lodgen;

import com.evandev.spicedcider.compat.voxy.VoxyIngest;
import com.evandev.spicedcider.networking.LodAckPayload;
import com.evandev.spicedcider.networking.LodChunkPayload;
import it.unimi.dsi.fastutil.longs.LongSet;
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
            queue.payloads.add(new Pending(payload, target.done()));
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
                drop(entry.getValue());
                continue;
            }
            Outgoing queue = entry.getValue();
            boolean paced = player.connection.hasChannel(LodAckPayload.TYPE);
            queue.budget = Math.min(queue.budget + chunksPerSecond / 20.0, Math.max(1.0, chunksPerSecond / 20.0) * 2);
            while (queue.budget >= 1.0 && (!paced || queue.pacer.canSend())) {
                Pending pending = queue.payloads.poll();
                if (pending == null) break;
                queue.size.decrementAndGet();
                if (player.level().dimension() != pending.payload().dimension()) {
                    pending.unmark();
                    continue;
                }
                PacketDistributor.sendToPlayer(player, pending.payload());
                if (paced) queue.pacer.sent();
                queue.budget -= 1.0;
            }
        }
    }

    void ack(UUID player) {
        Outgoing queue = outgoing.get(player);
        if (queue != null) queue.pacer.ack();
    }

    String pacing(UUID player) {
        Outgoing queue = outgoing.get(player);
        return queue == null ? "idle" : queue.pacer.describe();
    }

    void forget(MinecraftServer server, UUID player) {
        Outgoing queue = outgoing.remove(player);
        if (queue != null) drop(queue);
    }

    private static void drop(Outgoing queue) {
        Pending pending;
        while ((pending = queue.payloads.poll()) != null) pending.unmark();
    }

    void clear(MinecraftServer server) {
        outgoing.values().forEach(LodDelivery::drop);
        outgoing.clear();
    }

    private record Pending(LodChunkPayload payload, LongSet done) {
        void unmark() {
            done.remove(ChunkPos.asLong(payload.x(), payload.z()));
        }
    }

    private static final class Outgoing {
        final Queue<Pending> payloads = new ConcurrentLinkedQueue<>();
        final AtomicInteger size = new AtomicInteger();
        final LodPacer pacer = new LodPacer();
        double budget;
    }
}
