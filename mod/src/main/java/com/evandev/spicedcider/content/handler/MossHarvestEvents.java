package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class MossHarvestEvents {

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.mossNeedsHoe, false)
                && event.getBreaker() instanceof Player
                && event.getState().is(ModTags.Blocks.NEEDS_HOE_TO_HARVEST)
                && !event.getTool().isCorrectToolForDrops(event.getState())) {
            event.getDrops().clear();
        }
    }
}
