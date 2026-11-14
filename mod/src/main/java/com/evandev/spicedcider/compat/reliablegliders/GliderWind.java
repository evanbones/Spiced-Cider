package com.evandev.spicedcider.compat.reliablegliders;

import com.evandev.reliable_gliders.api.GlidingState;
import com.evandev.spicedcider.wind.client.ClientWind;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class GliderWind {
    private static final double ACCELERATION_PER_STRENGTH = 0.012;
    private static final double MAX_ACCELERATION = 0.02;

    public static void tick(Player player) {
        if (player.onGround() || player.isFallFlying() || player.isPassenger() || player.isInWaterOrBubble()
                || player.getAbilities().flying || !GlidingState.wasGliding(player)) return;
        float strength = ClientWind.exposedStrength(player.getX(), player.getY() + 1.0, player.getZ());
        if (strength < 0.02F) return;
        double acceleration = Math.min(MAX_ACCELERATION, strength * ACCELERATION_PER_STRENGTH);
        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(motion.x + ClientWind.directionX() * acceleration, motion.y, motion.z + ClientWind.directionZ() * acceleration);
    }
}
