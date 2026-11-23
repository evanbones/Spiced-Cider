package com.evandev.spicedcider.compat.personality;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.neoforged.fml.ModList;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PersonalitySitting {
    private static final Set<UUID> SERVER_SITTING = ConcurrentHashMap.newKeySet();
    private static Collection<?> clientSitting;

    public static void setSitting(Player player, boolean sitting) {
        if (sitting) {
            SERVER_SITTING.add(player.getUUID());
        } else {
            SERVER_SITTING.remove(player.getUUID());
        }
    }

    public static void forget(Player player) {
        SERVER_SITTING.remove(player.getUUID());
    }

    public static boolean isSitting(Player player) {
        if (!player.level().isClientSide()) {
            return SERVER_SITTING.contains(player.getUUID());
        }
        if (clientSitting == null) clientSitting = lookupClientSet();
        return clientSitting.contains(player.getUUID());
    }

    public static boolean isSittingOrSeated(Player player) {
        if (isSitting(player)) return true;
        Entity vehicle = player.getVehicle();
        return vehicle != null && !(vehicle instanceof LivingEntity) && !(vehicle instanceof VehicleEntity);
    }

    private static Collection<?> lookupClientSet() {
        if (!ModList.get().isLoaded("personality")) return List.of();
        try {
            Class<?> personality = Class.forName("com.teamabnormals.personality.core.Personality");
            return (Collection<?>) personality.getField("SYNCED_SITTING_PLAYERS").get(null);
        } catch (ReflectiveOperationException | ClassCastException e) {
            SpicedCider.LOGGER.warn("Could not read Personality's sitting players", e);
            return List.of();
        }
    }
}
