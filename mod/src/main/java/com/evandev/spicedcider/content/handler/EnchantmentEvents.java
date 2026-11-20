package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class EnchantmentEvents {

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker) || source.getDirectEntity() != attacker) return;
        int level = ModEnchantments.level(attacker, ModEnchantments.GUARD_BREAK, EquipmentSlot.MAINHAND);
        if (level <= 0) return;
        int ticks = event.getContainer().getPostAttackInvulnerabilityTicks();
        event.getContainer().setPostAttackInvulnerabilityTicks(Math.max(0, ticks - level));
    }

    @SubscribeEvent
    public static void onDamaged(LivingDamageEvent.Post event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide || event.getNewDamage() <= 0) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || attacker == victim) return;
        int level = ModEnchantments.level(victim, ModEnchantments.DISPLACEMENT, EquipmentSlot.CHEST);
        if (level <= 0 || attacker.getType().is(Tags.EntityTypes.BOSSES)) return;
        if (victim.getRandom().nextFloat() >= 0.2F * level) return;
        displace(attacker);
    }

    private static void displace(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        for (int i = 0; i < 16; i++) {
            double tx = x + (entity.getRandom().nextDouble() - 0.5) * 16.0;
            double ty = Mth.clamp(y + (entity.getRandom().nextInt(16) - 8), level.getMinBuildHeight(), level.getMinBuildHeight() + level.getLogicalHeight() - 1);
            double tz = z + (entity.getRandom().nextDouble() - 0.5) * 16.0;
            if (entity.isPassenger()) entity.stopRiding();
            if (entity.randomTeleport(tx, ty, tz, true)) {
                level.playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
                entity.resetFallDistance();
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide || entity.isSpectator()) return;
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.isEmpty() || ModEnchantments.level(entity.level(), ModEnchantments.BRACEWALK, legs) <= 0) return;
        Level level = entity.level();
        AABB box = entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).deflate(1.0E-7);
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(box.minX, box.minY, box.minZ), BlockPos.containing(box.maxX, box.maxY, box.maxZ))) {
            if (level.getBlockState(pos).getBlock() instanceof WebBlock) {
                level.destroyBlock(pos.immutable(), true, entity);
            }
        }
    }

    @SubscribeEvent
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        int level = ModEnchantments.level(event.getEntity().level(), ModEnchantments.QUICK_DRAW, event.getItem());
        if (level <= 0) return;
        int remaining = event.getDuration();
        if (remaining > 1 && remaining % Math.max(2, 5 - level) == 0) {
            event.setDuration(remaining - 1);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        List<ItemStack> kept = new ArrayList<>();
        Iterator<ItemEntity> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next().getItem();
            if (!stack.isEmpty() && ModEnchantments.level(player.level(), ModEnchantments.SOULBOUND, stack) > 0) {
                kept.add(stack.copy());
                iterator.remove();
            }
        }
        if (kept.isEmpty()) return;
        List<ItemStack> stored = new ArrayList<>(player.getData(ModAttachments.SOULBOUND_ITEMS));
        stored.addAll(kept);
        player.setData(ModAttachments.SOULBOUND_ITEMS, List.copyOf(stored));
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || !(event.getEntity() instanceof ServerPlayer player)) return;
        List<ItemStack> stored = player.getData(ModAttachments.SOULBOUND_ITEMS);
        if (stored.isEmpty()) return;
        player.setData(ModAttachments.SOULBOUND_ITEMS, List.of());
        for (ItemStack stack : stored) {
            ItemStack copy = stack.copy();
            if (!player.getInventory().add(copy) && !copy.isEmpty()) {
                player.drop(copy, false);
            }
        }
    }
}
