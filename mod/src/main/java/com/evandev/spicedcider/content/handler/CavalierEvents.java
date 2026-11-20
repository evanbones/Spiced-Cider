package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CavalierEvents {
    private static final ResourceLocation MOUNT_SPEED = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cavalier_mount_speed");
    private static final double SPEED_PER_LEVEL = 0.1;
    private static final Set<LivingEntity> BOOSTED = Collections.newSetFromMap(new WeakHashMap<>());

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.getMainHandItem().isEmpty()) return;
        if (!(player.getVehicle() instanceof LivingEntity mount) || BOOSTED.contains(mount)) return;
        if (update(mount)) BOOSTED.add(mount);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (BOOSTED.isEmpty()) return;
        Iterator<LivingEntity> iterator = BOOSTED.iterator();
        while (iterator.hasNext()) {
            if (!update(iterator.next())) iterator.remove();
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        BOOSTED.clear();
    }

    private static boolean update(LivingEntity mount) {
        AttributeInstance speed = mount.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return false;
        int level = mount.isRemoved() ? 0 : riderLevel(mount);
        AttributeModifier current = speed.getModifier(MOUNT_SPEED);
        if (level <= 0) {
            if (current != null) speed.removeModifier(MOUNT_SPEED);
            return false;
        }
        double amount = SPEED_PER_LEVEL * level;
        if (current == null || current.amount() != amount) {
            speed.addOrUpdateTransientModifier(new AttributeModifier(MOUNT_SPEED, amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        return true;
    }

    private static int riderLevel(LivingEntity mount) {
        int level = 0;
        for (Entity passenger : mount.getPassengers()) {
            if (passenger instanceof Player player) {
                level = Math.max(level, ModEnchantments.level(player, ModEnchantments.CAVALIER, EquipmentSlot.MAINHAND));
            }
        }
        return level;
    }
}
