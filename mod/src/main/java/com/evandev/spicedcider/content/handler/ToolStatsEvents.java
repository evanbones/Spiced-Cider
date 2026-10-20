package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.util.ToolStats;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.Tool;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ToolStatsEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applyToolStats(ModifyDefaultComponentsEvent event) {
        ToolStats table = ToolStats.get();
        if (table.isEmpty()) return;
        table.reportMissingIds();

        int modified = 0;
        for (Item item : event.getAllItems().toList()) {
            Optional<ToolStats.Stats> found = table.lookup(item);
            if (found.isEmpty()) continue;

            int maxDamage = item.components().getOrDefault(DataComponents.MAX_DAMAGE, 0);
            if (maxDamage <= 0 || maxDamage >= Integer.MAX_VALUE) continue;
            if (item.components().has(DataComponents.UNBREAKABLE)) continue;

            ToolStats.Stats stats = found.get();
            Tool tool = retunedTool(item, stats);
            event.modify(item, builder -> {
                builder.set(DataComponents.MAX_DAMAGE, stats.durability());
                if (tool != null) builder.set(DataComponents.TOOL, tool);
            });
            modified++;
        }
        SpicedCider.LOGGER.info("Tool stats: modified {} items", modified);
    }

    private static Tool retunedTool(Item item, ToolStats.Stats stats) {
        if (stats.speed().isEmpty()) return null;
        if (!(item instanceof TieredItem tiered) || item instanceof SwordItem) return null;
        Tool tool = item.components().get(DataComponents.TOOL);
        if (tool == null) return null;

        float oldSpeed = tiered.getTier().getSpeed();
        float newSpeed = stats.speed().get();
        boolean changed = false;
        List<Tool.Rule> rules = new ArrayList<>(tool.rules().size());
        for (Tool.Rule rule : tool.rules()) {
            if (rule.speed().isPresent() && Math.abs(rule.speed().get() - oldSpeed) < 1.0E-4F) {
                rules.add(new Tool.Rule(rule.blocks(), Optional.of(newSpeed), rule.correctForDrops()));
                changed = true;
            } else {
                rules.add(rule);
            }
        }
        return changed ? new Tool(rules, tool.defaultMiningSpeed(), tool.damagePerBlock()) : null;
    }
}
