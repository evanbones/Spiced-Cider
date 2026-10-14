package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.cavernsandchasms.TuningForkGolems;
import com.evandev.spicedcider.util.TuningForkNotes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class TuningForkEvents {

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || !(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        if (attacker instanceof Player player && TuningForkNotes.isFork(player.getMainHandItem()) && TuningForkGolems.isLoaded()) {
            TuningForkGolems.orderAttack(target, player);
        }
    }
}
