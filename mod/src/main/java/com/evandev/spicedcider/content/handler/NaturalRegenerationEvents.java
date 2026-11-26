package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class NaturalRegenerationEvents {
    private static final int INTERVAL_TICKS = 400;
    private static final int HURT_COOLDOWN_TICKS = 200;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel level)) return;
        if ((level.getGameTime() + player.getId()) % INTERVAL_TICKS != 0) return;
        int limit = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.naturalRegenerationLimit, 0);
        if (limit <= 0 || player.isDeadOrDying() || player.getHealth() >= Math.min(limit, player.getMaxHealth())) return;
        if (!level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) return;
        if (player.tickCount - player.getLastHurtByMobTimestamp() < HURT_COOLDOWN_TICKS && player.getLastHurtByMob() != null) return;
        player.heal(1.0F);
    }
}
