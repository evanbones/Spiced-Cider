package com.evandev.spicedcider.compat.getoverit;

import com.evandev.modest_meals.api.StaminaApi;
import com.evandev.modest_meals.stamina.PlayerStamina;
import com.evandev.modest_meals.stamina.StaminaHelper;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class LedgeClimbStamina {
    private static final float COST = 2.0F;

    private static boolean applies(Player player) {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.ledgeClimbStamina, false)
                && StaminaApi.isEnabled() && !player.isCreative() && !player.isSpectator();
    }

    public static boolean canClimb(Player player) {
        if (!applies(player)) return true;
        PlayerStamina stamina = StaminaHelper.get(player);
        return !stamina.getData().isExhausted() && stamina.getData().getStamina() > 0;
    }

    public static void spend(ServerPlayer player) {
        if (!applies(player)) return;
        PlayerStamina stamina = StaminaHelper.get(player);
        if (stamina.getData().isExhausted()) return;
        stamina.spend(COST);
        stamina.getData().setCooldown(stamina.getCooldownInTicks());
        stamina.syncNow();
    }
}
