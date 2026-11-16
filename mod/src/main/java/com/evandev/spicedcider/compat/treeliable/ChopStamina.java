package com.evandev.spicedcider.compat.treeliable;

import com.evandev.modest_meals.api.StaminaApi;
import com.evandev.modest_meals.stamina.PlayerStamina;
import com.evandev.modest_meals.stamina.StaminaHelper;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.treeliable.api.ChopEvent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;

public class ChopStamina {
    private static final float COST_PER_CHOP = 1.0F;

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ChopStamina::onStartChop);
    }

    private static void onStartChop(ChopEvent.StartChopEvent event) {
        Player player = event.getPlayer();
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.treeChoppingStamina, false)
                || !StaminaApi.isEnabled()
                || player.level().isClientSide()
                || player.isCreative() || player.isSpectator() || player instanceof FakePlayer) {
            return;
        }
        if (StaminaApi.isExhausted(player) || StaminaApi.getStamina(player) <= 0) {
            event.setCanceled(true);
            return;
        }
        PlayerStamina stamina = StaminaHelper.get(player);
        stamina.spend(COST_PER_CHOP);
        stamina.getData().setCooldown(stamina.getCooldownInTicks());
        stamina.syncNow();
    }
}
