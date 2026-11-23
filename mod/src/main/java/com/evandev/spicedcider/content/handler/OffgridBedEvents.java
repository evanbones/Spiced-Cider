package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.offgrid.OffgridBeds;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class OffgridBedEvents {
    private static final boolean OFFGRID = ModList.get().isLoaded("offgrid");

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (OFFGRID) OffgridBeds.flushClears();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!OFFGRID || event.isCanceled() || !(event.getLevel() instanceof ServerLevel level)) return;
        if (!OffgridBeds.placed(level, event.getPos(), event.getPlacedBlock())) event.setCanceled(true);
    }
}
