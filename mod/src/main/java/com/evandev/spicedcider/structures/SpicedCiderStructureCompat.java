package com.evandev.spicedcider.structures;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.command.SpicedCiderStructureCommand;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpicedCiderStructureCompat {

    public static void onServerStart(MinecraftServer server) {
        SpicedCiderStructureCommand.FEATURE_TIMINGS.clear();
        SpicedCiderStructureCommand.STRUCTURE_TIMINGS.clear();

        RegistryAccess.Frozen registryAccess = server.registryAccess();
        Registry<StructureSet> structureSetRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE_SET);

        if (SpicedCiderConfig.COMMON.logDuplicatedSalt.get()) {
            Int2ObjectOpenHashMap<Set<String>> structureSetIds = new Int2ObjectOpenHashMap<>();
            for (Map.Entry<ResourceKey<StructureSet>, StructureSet> entry : structureSetRegistry.entrySet()) {
                int salt = entry.getValue().placement().salt();
                structureSetIds.computeIfAbsent(salt, k -> new HashSet<>()).add(entry.getKey().location().toString());
            }

            for (Int2ObjectMap.Entry<Set<String>> entry : structureSetIds.int2ObjectEntrySet()) {
                if (entry.getValue().size() > 1) {
                    SpicedCider.LOGGER.warn("Non-unique structure_set salt:{} potentially creating overlapping structures detected. Structure sets: {}",
                            entry.getIntKey(), entry.getValue());
                }
            }
        }

    }
}