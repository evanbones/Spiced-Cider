package com.evandev.spicedcider.client.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InsufficientToolHandler {

    private static final Map<UUID, Long> lastAttemptTime = new HashMap<>();

    private InsufficientToolHandler() {
    }

    public static boolean requiresStrongerTool(Player player, BlockState state, ItemStack held) {
        if (!state.requiresCorrectToolForDrops()) return false;

        if (held.isEmpty() || !(held.getItem() instanceof TieredItem)) return false;

        if (held.isCorrectToolForDrops(state)) return false;

        return held.getDestroySpeed(state) > 1.0F;
    }

    public static boolean canAttemptMine(Player player) {
        return player.getAttackStrengthScale(0F) >= 1.0F;
    }

    public static void recordAttempt(Player player) {
        lastAttemptTime.put(player.getUUID(), player.level().getGameTime());
        player.resetAttackStrengthTicker();
    }
}
