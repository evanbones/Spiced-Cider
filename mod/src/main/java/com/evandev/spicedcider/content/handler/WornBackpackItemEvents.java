package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.util.CarriedItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class WornBackpackItemEvents {

    public static final TagKey<Item> TICKS_IN_WORN_BACKPACK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "ticks_in_worn_backpack"));

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.isSpectator()) {
            CarriedItems.forEachStored(player, stack -> {
                if (stack.is(TICKS_IN_WORN_BACKPACK)) stack.inventoryTick(player.level(), player, -1, false);
            });
        }
        if (player instanceof ServerPlayer serverPlayer) CarriedItems.writeBackLent(serverPlayer);
    }
}
