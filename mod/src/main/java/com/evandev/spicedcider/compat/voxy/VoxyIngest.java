package com.evandev.spicedcider.compat.voxy;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.lodgen.LodColumn;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public final class VoxyIngest {
    private static volatile boolean initialized;
    private static boolean available;
    private static MethodHandle worldIdentifier;
    private static MethodHandle rawIngest;
    private static MethodHandle instance;
    private static MethodHandle ingestService;
    private static MethodHandle taskCount;

    public static boolean available() {
        init();
        return available;
    }

    public static boolean ingest(Level level, LodColumn column) {
        if (!available()) return false;
        try {
            Object id = worldIdentifier.invoke(level);
            if (id == null || instance.invoke() == null) return false;
            for (LodColumn.Section section : column.sections()) {
                boolean accepted = (boolean) rawIngest.invoke(id, section.section(), column.x(), section.y(), column.z(),
                        section.blockLight(), section.skyLight());
                if (!accepted) return false;
            }
            return true;
        } catch (Throwable throwable) {
            SpicedCider.LOGGER.error("Voxy LoD ingest failed for chunk {}, {}", column.x(), column.z(), throwable);
            return false;
        }
    }

    public static int backlog() {
        if (!available()) return -1;
        try {
            Object voxy = instance.invoke();
            return voxy == null ? -1 : (int) taskCount.invoke(ingestService.invoke(voxy));
        } catch (Throwable throwable) {
            return -1;
        }
    }

    private static void init() {
        if (initialized) return;
        synchronized (VoxyIngest.class) {
            if (initialized) return;
            try {
                MethodHandles.Lookup lookup = MethodHandles.publicLookup();
                Class<?> identifier = Class.forName("me.cortex.voxy.commonImpl.WorldIdentifier");
                Class<?> service = Class.forName("me.cortex.voxy.common.world.service.VoxelIngestService");
                Class<?> common = Class.forName("me.cortex.voxy.commonImpl.VoxyCommon");
                Class<?> voxyInstance = Class.forName("me.cortex.voxy.commonImpl.VoxyInstance");
                worldIdentifier = lookup.unreflect(identifier.getMethod("of", Level.class));
                rawIngest = lookup.unreflect(service.getMethod("rawIngest", identifier, LevelChunkSection.class,
                        int.class, int.class, int.class, DataLayer.class, DataLayer.class));
                instance = lookup.unreflect(common.getMethod("getInstance"));
                ingestService = lookup.unreflect(voxyInstance.getMethod("getIngestService"));
                taskCount = lookup.unreflect(service.getMethod("getTaskCount"));
                available = true;
            } catch (ClassNotFoundException ignored) {
            } catch (ReflectiveOperationException | LinkageError exception) {
                SpicedCider.LOGGER.warn("Voxy is installed but its ingest API did not match", exception);
            } finally {
                initialized = true;
            }
        }
    }
}
