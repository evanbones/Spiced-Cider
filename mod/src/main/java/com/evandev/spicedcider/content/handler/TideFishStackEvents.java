package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.tide.TideCompat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class TideFishStackEvents {

    @SubscribeEvent
    public static void onItemJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getEntity() instanceof ItemEntity item) || !TideCompat.isLoaded()) return;
        TideCompat.stripBucketableMarker(item.getItem());
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!TideCompat.isLoaded()) return;
        Player player = event.getEntity();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            TideCompat.stripBucketableMarker(player.getInventory().getItem(i));
        }
        player.inventoryMenu.broadcastChanges();
    }
}
