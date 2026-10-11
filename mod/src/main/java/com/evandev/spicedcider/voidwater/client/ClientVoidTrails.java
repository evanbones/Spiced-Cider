package com.evandev.spicedcider.voidwater.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.networking.VoidTrailPayload;
import com.evandev.spicedcider.voidwater.VoidTrailColumns;
import com.evandev.spicedcider.voidwater.VoidTrailLevel;
import com.evandev.spicedcider.voidwater.VoidTrails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class ClientVoidTrails {

    public static void receive(VoidTrailPayload payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        VoidTrailColumns columns = VoidTrails.columns(level);
        if (columns == null) return;

        int sectionY = level.getMinBuildHeight() >> 4;
        long lastDirty = Long.MIN_VALUE;
        for (int i = 0; i < payload.columns().length; i++) {
            long column = payload.columns()[i];
            int x = VoidTrails.unpackX(column);
            int z = VoidTrails.unpackZ(column);
            if (!columns.set(x, z, payload.lengths()[i] & 0xFF)) continue;
            long chunk = ChunkPos.asLong(x >> 4, z >> 4);
            if (chunk != lastDirty) {
                lastDirty = chunk;
                Minecraft.getInstance().levelRenderer.setSectionDirty(x >> 4, sectionY, z >> 4);
            }
        }
    }

    public static boolean hidesBottomFace(BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        return level != null && pos.getY() == level.getMinBuildHeight() && VoidTrails.lengthAt(level, pos.getX(), pos.getZ()) > 0;
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level && VoidTrails.enabledIn(level)) {
            ((VoidTrailLevel) level).spicedcider$setVoidTrails(new VoidTrailColumns(), null);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel level) {
            VoidTrailColumns columns = VoidTrails.columns(level);
            if (columns != null) columns.removeChunk(event.getChunk().getPos().toLong());
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        VoidTrailColumns columns = VoidTrails.columns(level);
        if (columns == null || columns.chunks().isEmpty()) return;

        Vec3 camera = event.getCamera().getPosition();
        boolean below = camera.y < level.getMinBuildHeight();
        RenderLevelStageEvent.Stage stage = event.getStage();
        if (below ? stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS : stage == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            VoidTrailRenderer.render(event, level, columns, camera);
        }
    }
}
