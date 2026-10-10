package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class TooltipCleanupEvents {

    private static final Set<String> HIDDEN_KEYS = Set.of(
            "quark.misc.repaired",
            "tooltip.bountifulfares.dyeable",
            "item.chalk.chalk.tooltip.dyeable"
    );

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        List<Component> tooltip = event.getToolTip();
        if (tooltip.size() > 1) {
            tooltip.subList(1, tooltip.size()).removeIf(TooltipCleanupEvents::isHidden);
        }
    }

    private static boolean isHidden(Component component) {
        if (component.getContents() instanceof TranslatableContents contents && HIDDEN_KEYS.contains(contents.getKey())) {
            return true;
        }
        for (Component sibling : component.getSiblings()) {
            if (isHidden(sibling)) return true;
        }
        return false;
    }
}
