package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.block.entity.GraveBlockEntity;
import com.evandev.spicedcider.content.gravestone.GraveHistory;
import com.evandev.spicedcider.content.gravestone.Graves;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.util.CarriedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class GravestoneEvents {
    private static final Map<ServerPlayer, Pending> PENDING = new WeakHashMap<>();

    private static class Pending {
        private final List<GraveBlockEntity.Entry> slots;
        private int experience;

        private Pending(List<GraveBlockEntity.Entry> slots) {
            this.slots = slots;
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.getServer() != null) {
            GraveHistory.get(player.getServer()).sync(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.isSpectator()) return;
        Inventory inventory = player.getInventory();
        if (!CarriedItems.has(player, GravestoneEvents::isGravestone)) {
            PENDING.remove(player);
            return;
        }
        List<GraveBlockEntity.Entry> slots = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) slots.add(new GraveBlockEntity.Entry(i, stack.copy()));
        }
        PENDING.put(player, new Pending(slots));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Pending pending = PENDING.get(player);
        if (pending == null || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.gravestoneStoreExperience, true)) return;
        Collection<ItemEntity> captured = player.captureDrops();
        if (captured == null || findGravestone(captured) == null && findHolder(captured) == null) return;
        int experience = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.gravestoneKeepAllExperience, false)
                ? totalExperience(player)
                : event.getDroppedExperience();
        pending.experience += experience;
        event.setDroppedExperience(0);
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Pending pending = PENDING.remove(player);
        if (event.isCanceled()) {
            refundExperience(player, pending);
            return;
        }
        BlockPos grave = null;
        Collection<ItemEntity> drops = event.getDrops();
        ItemEntity gravestone = pending == null ? null : findGravestone(drops);
        ItemEntity holder = pending == null || gravestone != null ? null : findHolder(drops);
        if (gravestone != null || holder != null) {
            ItemStack holderBackup = holder == null ? ItemStack.EMPTY : holder.getItem().copy();
            if (gravestone != null) {
                gravestone.getItem().shrink(1);
                if (gravestone.getItem().isEmpty()) drops.remove(gravestone);
            } else {
                CarriedItems.removeOne(holder.getItem(), GravestoneEvents::isGravestone);
            }
            List<GraveBlockEntity.Entry> entries = entries(drops, pending.slots);
            grave = entries.isEmpty() && pending.experience <= 0 ? null : Graves.create(player, entries, pending.experience);
            if (grave != null) {
                drops.clear();
            } else {
                if (gravestone != null) {
                    if (gravestone.getItem().isEmpty()) drops.add(gravestone);
                    gravestone.getItem().grow(1);
                } else {
                    holder.setItem(holderBackup);
                }
                refundExperience(player, pending);
            }
        } else {
            refundExperience(player, pending);
        }
        Graves.deathDecay(player, grave);
    }

    private static void refundExperience(ServerPlayer player, @Nullable Pending pending) {
        if (pending != null && pending.experience > 0) {
            ExperienceOrb.award(player.serverLevel(), player.position(), pending.experience);
            pending.experience = 0;
        }
    }

    private static boolean isGravestone(ItemStack stack) {
        return stack.is(ModItems.GRAVESTONE.get());
    }

    @Nullable
    private static ItemEntity findHolder(Collection<ItemEntity> drops) {
        for (ItemEntity drop : drops) {
            if (CarriedItems.contains(drop.getItem(), GravestoneEvents::isGravestone)) return drop;
        }
        return null;
    }

    @Nullable
    private static ItemEntity findGravestone(Collection<ItemEntity> drops) {
        for (ItemEntity drop : drops) {
            if (drop.getItem().is(ModItems.GRAVESTONE.get())) return drop;
        }
        return null;
    }

    private static List<GraveBlockEntity.Entry> entries(Collection<ItemEntity> drops, List<GraveBlockEntity.Entry> slots) {
        List<GraveBlockEntity.Entry> unclaimed = new ArrayList<>(slots);
        List<GraveBlockEntity.Entry> entries = new ArrayList<>();
        for (ItemEntity drop : drops) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) continue;
            GraveBlockEntity.Entry match = match(unclaimed, stack, true);
            if (match == null) match = match(unclaimed, stack, false);
            if (match != null) unclaimed.remove(match);
            entries.add(new GraveBlockEntity.Entry(match == null ? -1 : match.slot(), stack.copy()));
        }
        return entries;
    }

    @Nullable
    private static GraveBlockEntity.Entry match(List<GraveBlockEntity.Entry> slots, ItemStack stack, boolean exactCount) {
        for (GraveBlockEntity.Entry entry : slots) {
            if (ItemStack.isSameItemSameComponents(entry.stack(), stack) && (!exactCount || entry.stack().getCount() == stack.getCount())) {
                return entry;
            }
        }
        return null;
    }

    private static int totalExperience(Player player) {
        int level = player.experienceLevel;
        int fromLevels;
        if (level <= 16) {
            fromLevels = level * level + 6 * level;
        } else if (level <= 31) {
            fromLevels = (int) (2.5F * level * level - 40.5F * level + 360);
        } else {
            fromLevels = (int) (4.5F * level * level - 162.5F * level + 2220);
        }
        return fromLevels + Math.round(player.getXpNeededForNextLevel() * player.experienceProgress);
    }
}
