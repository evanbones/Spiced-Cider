package com.evandev.spicedcider.compat.lodged;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.jvn.lodged.config.LodgedConfig;
import com.jvn.lodged.world.LodgedArrowStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class TamedArrowPulling {
    private static final int REQUEST_COOLDOWN_TICKS = 4;
    private static final double REACH_PADDING = 1.5;
    private static final Map<ServerPlayer, Long> LAST_REQUEST = new WeakHashMap<>();
    private static MethodHandle removeMobBodyArrow;

    public static boolean enabled() {
        return SpicedCiderConfig.COMMON_SPEC.isLoaded() && SpicedCiderConfig.COMMON.lodgedTamedArrowPulling.get();
    }

    public static boolean canPull(Player player, Entity target) {
        if (!(target instanceof LivingEntity mob) || !(target instanceof OwnableEntity ownable)) return false;
        if (mob == player || !mob.isAlive() || !player.isAlive() || player.isSpectator()) return false;
        if (player.isCreative() && !LodgedConfig.allowArrowRemovalInCreative()) return false;
        UUID owner = ownable.getOwnerUUID();
        boolean tamed = switch (target) {
            case TamableAnimal tamable -> tamable.isTame();
            case AbstractHorse horse -> horse.isTamed();
            default -> owner != null;
        };
        return tamed && (!LodgedConfig.requireMobOwnership() || player.getUUID().equals(owner));
    }

    public static void handle(ServerPlayer player, int entityId, int arrowIndex) {
        if (!enabled()) return;
        Entity target = player.level().getEntity(entityId);
        if (!canPull(player, target) || !player.canInteractWithEntity(target, REACH_PADDING) || !player.hasLineOfSight(target)) return;
        long now = player.level().getGameTime();
        Long last = LAST_REQUEST.get(player);
        if (last != null && now - last < REQUEST_COOLDOWN_TICKS && now >= last) return;
        LivingEntity mob = (LivingEntity) target;
        if (arrowIndex < 0 || arrowIndex >= LodgedArrowStorage.readAll(mob).size()) return;
        LAST_REQUEST.put(player, now);
        try {
            removeMobBodyArrow().invoke(player, mob, arrowIndex);
        } catch (Throwable e) {
            throw new IllegalStateException("Lodged arrow removal failed", e);
        }
    }

    private static MethodHandle removeMobBodyArrow() throws ReflectiveOperationException {
        if (removeMobBodyArrow == null) {
            Class<?> outcomes = Class.forName("com.jvn.lodged.network.ArrowRemovalOutcomes");
            Method method = outcomes.getDeclaredMethod("removeMobBodyArrow", ServerPlayer.class, LivingEntity.class, int.class);
            method.setAccessible(true);
            removeMobBodyArrow = MethodHandles.lookup().unreflect(method);
        }
        return removeMobBodyArrow;
    }
}
