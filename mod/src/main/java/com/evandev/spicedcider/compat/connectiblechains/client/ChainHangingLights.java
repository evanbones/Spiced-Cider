package com.evandev.spicedcider.compat.connectiblechains.client;

import com.evandev.connectiblechains.entity.ChainKnotEntity;
import com.evandev.connectiblechains.entity.Chainable;
import com.evandev.connectiblechains.util.HangingBlockPlacement;
import com.evandev.connectiblechains.util.HangingLightHelper;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.mehvahdjukaar.polytone.api.VoxelVolumeApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public final class ChainHangingLights {
    private static final int SCAN_INTERVAL = 10;

    private static volatile Long2ObjectMap<BlockState> cells = Long2ObjectMaps.emptyMap();
    private static int ticks;

    public static BlockState lightAt(int x, int y, int z, BlockState state) {
        if (!state.is(Blocks.LIGHT)) return state;
        Long2ObjectMap<BlockState> current = cells;
        if (current.isEmpty()) return state;
        BlockState hanging = current.get(BlockPos.asLong(x, y, z));
        return hanging != null ? hanging : state;
    }

    public static void tick() {
        if (++ticks < SCAN_INTERVAL) return;
        ticks = 0;
        ClientLevel level = Minecraft.getInstance().level;
        Long2ObjectMap<BlockState> found = level != null
                && SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.matchChainLightColors, false)
                ? scan(level) : Long2ObjectMaps.emptyMap();
        Long2ObjectMap<BlockState> previous = cells;
        if (previous.isEmpty() && found.isEmpty()) return;
        cells = found;
        for (Long2ObjectMap.Entry<BlockState> entry : found.long2ObjectEntrySet()) {
            if (!Objects.equals(previous.get(entry.getLongKey()), entry.getValue())) {
                VoxelVolumeApi.markDirty(BlockPos.of(entry.getLongKey()));
            }
        }
        for (long key : previous.keySet()) {
            if (!found.containsKey(key)) VoxelVolumeApi.markDirty(BlockPos.of(key));
        }
    }

    public static void clear() {
        cells = Long2ObjectMaps.emptyMap();
        ticks = 0;
    }

    private static Long2ObjectMap<BlockState> scan(ClientLevel level) {
        Long2ObjectMap<BlockState> found = new Long2ObjectOpenHashMap<>();
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof ChainKnotEntity knot)) continue;
            for (Chainable.ChainData chainData : knot.getChainDataArray()) {
                if (chainData.hangings.isEmpty()) continue;
                Entity holder = knot.getChainHolder(chainData);
                if (holder == null) continue;
                float slack = chainData.getSlack();
                for (Chainable.ChainData.HangingEntry entry : chainData.hangings) {
                    BlockState state = HangingBlockPlacement.hangingState(entry.blockId());
                    if (state == null || state.getLightEmission() <= 0) continue;
                    BlockPos pos = HangingLightHelper.computeLightPos(knot, holder, entry.t(), slack);
                    if (pos != null) found.put(pos.asLong(), state);
                }
            }
        }
        return found.isEmpty() ? Long2ObjectMaps.emptyMap() : found;
    }
}
