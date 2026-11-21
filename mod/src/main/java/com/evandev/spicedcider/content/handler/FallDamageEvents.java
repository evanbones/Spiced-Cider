package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class FallDamageEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        int reduction = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.fallDamageReduction, 0);
        if (reduction <= 0) return;
        event.setDistance(Math.max(0.0F, event.getDistance() - reduction));
    }
}
