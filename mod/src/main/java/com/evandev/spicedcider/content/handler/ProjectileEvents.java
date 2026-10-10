package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ProjectileEvents {

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getDirectSourceEntity() instanceof LargeFireball) {
            event.getAffectedBlocks().clear();
        }
    }
}
