package com.evandev.spicedcider.compat.upgradeaquatic;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.minecraft.LivingEntityJumpingAccessor;
import com.evandev.spicedcider.mixin.upgradeaquatic.ThrasherAccessor;
import com.evandev.spicedcider.networking.ThrasherStrugglePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ThrasherStruggle {
    private static final float PROGRESS_PER_PRESS = 0.1F;
    private static final float DECAY_PER_TICK = 0.008F;
    private static final float BITE_ESCAPE_CHANCE = 0.1F;
    private static final int STUN_TICKS = 50;
    private static final int RELEASE_TIMEOUT = 10;
    private static final Map<ServerPlayer, State> STATES = new WeakHashMap<>();
    private static final Map<ServerPlayer, Release> RELEASES = new WeakHashMap<>();

    private record Release(Entity thrasher, int deadline) {
    }

    private static final class State {
        float progress;
        boolean wasJumping = true;
        int sent = -1;
    }

    private static boolean canStruggle(ServerPlayer player) {
        return player.getVehicle() instanceof ThrasherAccessor
                && !player.isCreative() && !player.isSpectator()
                && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.thrasherStruggling, false);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Release release = RELEASES.get(player);
        if (release != null) {
            if (player.getVehicle() != release.thrasher()) {
                RELEASES.remove(player);
                pushApart(player, release.thrasher());
            } else if (player.tickCount >= release.deadline()) {
                RELEASES.remove(player);
            }
            return;
        }
        State state = STATES.get(player);
        if (!canStruggle(player)) {
            if (state != null) {
                STATES.remove(player);
                send(player, -1.0F);
            }
            return;
        }
        if (state == null) {
            state = new State();
            STATES.put(player, state);
            player.displayClientMessage(Component.translatable("message.spicedcider.thrasher_struggle", Component.keybind("key.jump")), true);
        }
        boolean jumping = ((LivingEntityJumpingAccessor) player).spicedcider$isJumping();
        if (jumping && !state.wasJumping) {
            state.progress += PROGRESS_PER_PRESS;
        } else {
            state.progress = Math.max(0.0F, state.progress - DECAY_PER_TICK);
        }
        state.wasJumping = jumping;
        if (state.progress >= 1.0F) {
            breakFree(player, player.getVehicle());
            return;
        }
        int quantized = Math.round(state.progress * 100.0F);
        if (quantized != state.sent) {
            state.sent = quantized;
            send(player, state.progress);
        }
    }

    @SubscribeEvent
    public static void onDamaged(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isAlive() || !canStruggle(player)) return;
        Entity thrasher = player.getVehicle();
        if (event.getSource().getEntity() == thrasher && player.getRandom().nextFloat() < BITE_ESCAPE_CHANCE) {
            breakFree(player, thrasher);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            STATES.remove(player);
            RELEASES.remove(player);
        }
    }

    private static void breakFree(ServerPlayer player, Entity thrasher) {
        STATES.remove(player);
        send(player, -1.0F);
        ((ThrasherAccessor) thrasher).spicedcider$setStunned(STUN_TICKS);
        RELEASES.put(player, new Release(thrasher, player.tickCount + RELEASE_TIMEOUT));
    }

    private static void pushApart(ServerPlayer player, Entity thrasher) {
        Vec3 away = thrasher.position().subtract(player.position()).multiply(1.0, 0.0, 1.0);
        if (away.lengthSqr() < 1.0E-4) away = Vec3.directionFromRotation(0.0F, thrasher.getYRot());
        away = away.normalize();
        thrasher.setDeltaMovement(thrasher.getDeltaMovement().add(away.x * 1.2, 0.15, away.z * 1.2));
        thrasher.hurtMarked = true;
        player.setDeltaMovement(player.getDeltaMovement().add(-away.x * 0.4, 0.2, -away.z * 0.4));
        player.hurtMarked = true;
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS, 1.0F, 0.8F);
    }

    private static void send(ServerPlayer player, float progress) {
        PacketDistributor.sendToPlayer(player, new ThrasherStrugglePayload(progress));
    }
}
