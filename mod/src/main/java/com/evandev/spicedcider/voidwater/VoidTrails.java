package com.evandev.spicedcider.voidwater;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class VoidTrails {

    public static long pack(int x, int z) {
        return (long) x << 32 | z & 0xFFFFFFFFL;
    }

    public static int unpackX(long packed) {
        return (int) (packed >> 32);
    }

    public static int unpackZ(long packed) {
        return (int) packed;
    }

    public static int maxLength() {
        return SpicedCiderConfig.COMMON.voidWaterTrailLength.get();
    }

    public static boolean enabledIn(Level level) {
        return SpicedCiderConfig.COMMON.voidWater.get()
                && !SpicedCiderConfig.COMMON.voidWaterExcludedDimensions.get().contains(level.dimension().location().toString());
    }

    @Nullable
    public static VoidTrailColumns columns(Level level) {
        return ((VoidTrailLevel) level).spicedcider$voidTrails();
    }

    public static int lengthAt(Level level, int x, int z) {
        VoidTrailColumns columns = columns(level);
        return columns == null ? 0 : columns.get(x, z);
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && enabledIn(level)) {
            ServerVoidTrails trails = level.getDataStorage().computeIfAbsent(ServerVoidTrails.FACTORY, ServerVoidTrails.DATA_NAME);
            ((VoidTrailLevel) level).spicedcider$setVoidTrails(trails.columns(), trails);
        }
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        BlockPos pos = event.getPos();
        if (pos.getY() != level.getMinBuildHeight()) return;
        ServerVoidTrails trails = ((VoidTrailLevel) level).spicedcider$serverVoidTrails();
        if (trails != null) trails.activate(pos.getX(), pos.getZ());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ServerVoidTrails trails = ((VoidTrailLevel) level).spicedcider$serverVoidTrails();
        if (trails != null) trails.tick(level);
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerLevel level = event.getLevel();
        ServerVoidTrails trails = ((VoidTrailLevel) level).spicedcider$serverVoidTrails();
        if (trails != null) trails.watch(event.getPlayer(), event.getChunk(), level);
    }
}
