package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.networking.CrudeArrowPayload;
import com.evandev.spicedcider.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CrudeArrows {

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof AbstractArrow arrow && arrow.hasData(ModAttachments.CRUDE_ARROW)
                && event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new CrudeArrowPayload(arrow.getId()));
        }
    }

}
