package com.evandev.spicedcider.locate;

import com.evandev.spicedcider.mixin.minecraft.StructureCheckAccessor;
import com.evandev.spicedcider.mixin.minecraft.StructureManagerAccessor;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.Holder;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class StructurePrefetch {
    private static final int MIN_SLICE = 1;

    public static void warmRing(StructureManager manager, Set<Holder<Structure>> structures, int centerX, int centerZ, int ring,
                                long seed, RandomSpreadStructurePlacement placement) {
        List<Candidate> candidates = new ArrayList<>();
        int spacing = placement.spacing();
        for (int dx = -ring; dx <= ring; dx++) {
            boolean edgeX = dx == -ring || dx == ring;
            for (int dz = -ring; dz <= ring; dz++) {
                if (!edgeX && dz != -ring && dz != ring) continue;
                ChunkPos chunk = placement.getPotentialStructureChunk(seed, centerX + spacing * dx, centerZ + spacing * dz);
                for (Holder<Structure> structure : structures) candidates.add(new Candidate(chunk, structure.value(), placement));
            }
        }
        warm(manager, candidates);
    }

    public static void warm(StructureManager manager, List<Candidate> candidates) {
        StructureCheckAccessor check = (StructureCheckAccessor) ((StructureManagerAccessor) manager).spicedcider$structureCheck();
        Map<Structure, Long2BooleanMap> featureChecks = check.spicedcider$featureChecks();
        long seed = check.spicedcider$seed();
        Map<Structure, LongSet> queued = new HashMap<>();
        List<Candidate> pending = new ArrayList<>();
        for (Candidate candidate : candidates) {
            long key = candidate.chunk.toLong();
            if (check.spicedcider$loadedChunks().containsKey(key)) continue;
            Long2BooleanMap known = featureChecks.get(candidate.structure);
            if (known != null && known.containsKey(key)) continue;
            if (!candidate.placement.applyAdditionalChunkRestrictions(candidate.chunk.x, candidate.chunk.z, seed)) continue;
            if (!queued.computeIfAbsent(candidate.structure, structure -> new LongOpenHashSet()).add(key)) continue;
            pending.add(candidate);
        }
        pending.removeIf(new StoredChunks(check.spicedcider$storageAccess(), pending)::contains);
        if (pending.size() < 2) return;
        byte[] results = new byte[pending.size()];
        try {
            LocateWorkers.forEachSlice(pending.size(), MIN_SLICE, (from, to) -> {
                for (int i = from; i < to; i++) {
                    Candidate candidate = pending.get(i);
                    try {
                        results[i] = check.spicedcider$canCreateStructure(candidate.chunk, candidate.structure) ? (byte) 1 : (byte) 2;
                    } catch (RuntimeException ignored) {
                    }
                }
            });
        } catch (LocateWorkers.LocateFailure failure) {
            LocateWorkers.logFallback(failure.getCause());
        }
        for (int i = 0; i < results.length; i++) {
            if (results[i] == 0) continue;
            Candidate candidate = pending.get(i);
            featureChecks.computeIfAbsent(candidate.structure, structure -> new Long2BooleanOpenHashMap()).putIfAbsent(candidate.chunk.toLong(), results[i] == 1);
        }
    }

    public record Candidate(ChunkPos chunk, Structure structure, StructurePlacement placement) {
    }

    private static final class StoredChunks {
        private final LongSet stored = new LongOpenHashSet();

        StoredChunks(ChunkScanAccess storage, List<Candidate> candidates) {
            Map<Long, CollectFields> scans = new HashMap<>();
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Candidate candidate : candidates) {
                long key = candidate.chunk.toLong();
                if (scans.containsKey(key)) continue;
                CollectFields fields = new CollectFields(new FieldSelector(IntTag.TYPE, "DataVersion"));
                scans.put(key, fields);
                futures.add(storage.scanChunk(candidate.chunk, fields).exceptionally(failure -> {
                    synchronized (this.stored) {
                        this.stored.add(key);
                    }
                    return null;
                }));
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
            scans.forEach((key, fields) -> {
                if (fields.getResult() != null) this.stored.add(key);
            });
        }

        boolean contains(Candidate candidate) {
            return this.stored.contains(candidate.chunk.toLong());
        }
    }
}
