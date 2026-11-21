package com.evandev.spicedcider.content.enchantment;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.mixin.minecraft.AbstractArrowAccessor;
import com.evandev.spicedcider.registry.ModEnchantments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ProjectileEnchantments {
    private static final double SEEK_RANGE = 10.0;
    private static final int STRAIGHT_SHOT_TICKS = 100;

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk() || event.getLevel().isClientSide() || !(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (ModEnchantments.level(event.getLevel(), ModEnchantments.STRAIGHT_SHOT, arrow.getWeaponItem()) > 0) {
            arrow.setNoGravity(true);
        }
    }

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow) || !(arrow.level() instanceof ServerLevel level)) return;
        ItemStack weapon = arrow.getWeaponItem();
        if (weapon == null || weapon.isEmpty() || ((AbstractArrowAccessor) arrow).spicedcider$inGround()) return;
        if (arrow.isNoGravity() && arrow.tickCount > STRAIGHT_SHOT_TICKS && ModEnchantments.level(level, ModEnchantments.STRAIGHT_SHOT, weapon) > 0) {
            arrow.setNoGravity(false);
        }
        if (arrow.tickCount < 3 || arrow.isNoPhysics()) return;
        if (ModEnchantments.level(level, ModEnchantments.SEEKING, weapon) <= 0) return;
        if (arrow instanceof ThrownTrident && !arrow.isInWaterRainOrBubble()) return;
        Vec3 motion = arrow.getDeltaMovement();
        double speed = motion.length();
        if (speed < 0.1) return;
        LivingEntity target = findTarget(level, arrow, motion.normalize());
        if (target == null) return;
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(arrow.position()).normalize();
        Vec3 steered = motion.normalize().scale(0.6).add(toTarget.scale(0.5)).normalize().scale(speed);
        arrow.setDeltaMovement(steered);
        arrow.hasImpulse = true;
        if (level.random.nextFloat() < 0.1F) {
            level.sendParticles(ParticleTypes.SCULK_SOUL, arrow.getX(), arrow.getY(), arrow.getZ(), 1, 0, 0, 0, 0);
        } else {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, arrow.getX(), arrow.getY(), arrow.getZ(), 1, 0, 0, 0, 0);
        }
    }

    @Nullable
    private static LivingEntity findTarget(ServerLevel level, AbstractArrow arrow, Vec3 heading) {
        Entity owner = arrow.getOwner();
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, arrow.getBoundingBox().inflate(SEEK_RANGE),
                entity -> isValidTarget(entity, owner, arrow, heading));
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : candidates) {
            if (entity.isCurrentlyGlowing()) return entity;
            double distance = entity.distanceToSqr(arrow);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = entity;
            }
        }
        return best;
    }

    private static boolean isValidTarget(LivingEntity entity, @Nullable Entity owner, AbstractArrow arrow, Vec3 heading) {
        if (!entity.isAlive() || entity.isSpectator() || !entity.isPickable() || entity == owner) return false;
        if (owner != null) {
            if (entity.isAlliedTo(owner)) return false;
            if (entity instanceof OwnableEntity ownable && owner.getUUID().equals(ownable.getOwnerUUID())) return false;
        }
        if (entity.isCurrentlyGlowing()) return true;
        Vec3 toTarget = entity.position().subtract(arrow.position()).normalize();
        return heading.dot(toTarget) > 0.5;
    }
}
