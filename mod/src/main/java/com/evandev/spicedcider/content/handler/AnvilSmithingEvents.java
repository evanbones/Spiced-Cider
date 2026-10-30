package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.smithing.AnvilSmithing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class AnvilSmithingEvents {

    @SubscribeEvent
    public static void smithAtAnvil(AnvilUpdateEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack result = AnvilSmithing.result(player.level(), event.getLeft(), event.getRight());
        if (result.isEmpty()) return;

        event.setOutput(result);
        event.setCost(0);
        event.setMaterialCost(1);
    }
}
