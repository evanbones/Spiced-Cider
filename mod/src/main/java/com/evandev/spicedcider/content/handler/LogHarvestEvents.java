package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class LogHarvestEvents {

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (event.getTargetBlock().is(ModTags.Blocks.NEEDS_AXE_TO_HARVEST)
                && !event.getEntity().getMainHandItem().isCorrectToolForDrops(event.getTargetBlock())) {
            event.setCanHarvest(false);
        }
    }
}
