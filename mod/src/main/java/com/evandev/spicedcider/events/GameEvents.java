package com.evandev.spicedcider.events;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class GameEvents {

    @SubscribeEvent
    public static void preventRedstonePlacement(PlayerInteractEvent.RightClickBlock event) {
        if (event.getItemStack().is(Items.REDSTONE)) {
            if (!SpicedCiderConfig.COMMON.allowRedstonePlacement.get()) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
    }
}