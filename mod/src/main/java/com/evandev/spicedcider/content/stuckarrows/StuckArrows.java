package com.evandev.spicedcider.content.stuckarrows;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.networking.StuckArrowsPayload;
import com.evandev.spicedcider.registry.ModAttachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public final class StuckArrows {
    public static final int HEAD = 0;
    public static final int BODY = 1;
    public static final int LIMB = 2;

    private static final float[] PULL_CHANCE = {0.5F, 0.72F, 0.9F};
    private static final float BREAK_DAMAGE = 1.0F;
    private static final double HIT_INFLATE = 0.3;
    private static final int REQUEST_COOLDOWN_TICKS = 4;
    private static final double REACH_PADDING = 1.5;
    private static final TagKey<EntityType<?>> NO_STUCK_ARROWS =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "no_stuck_arrows"));
    private static final Map<ServerPlayer, Long> LAST_REQUEST = new WeakHashMap<>();

    public static boolean enabled() {
        return SpicedCiderConfig.COMMON_SPEC.isLoaded() && SpicedCiderConfig.feature("stuckArrows");
    }

    public static boolean tracks(LivingEntity entity) {
        return enabled() && !entity.getType().is(NO_STUCK_ARROWS);
    }

    public static void onStuck(LivingEntity entity, AbstractArrow arrow) {
        StuckArrowSlots slots = entity.getData(ModAttachments.STUCK_ARROWS);
        Vec3 start = arrow.position();
        Vec3 motion = arrow.getDeltaMovement();
        Vec3 hitLocation = entity.getBoundingBox().inflate(HIT_INFLATE).clip(start, start.add(motion.scale(2.0))).orElse(start);
        if (slots.add(recoveredStack(arrow), bodyFrameHit(entity, hitLocation, motion))) {
            changed(entity, slots);
        }
    }

    private static List<Float> bodyFrameHit(LivingEntity entity, Vec3 location, Vec3 motion) {
        if (motion.lengthSqr() < 1.0E-6) return List.of();
        Quaternionf toBody = new Quaternionf().rotationY((float) Math.toRadians(180.0F - entity.yBodyRot)).conjugate();
        float scale = Math.max(entity.getScale(), 0.01F);
        Vector3f point = toBody.transform(new Vector3f((float) (location.x - entity.getX()), (float) (location.y - entity.getY()), (float) (location.z - entity.getZ()))).div(scale);
        Vector3f direction = toBody.transform(motion.toVector3f()).normalize();
        return List.of(point.x, point.y, point.z, direction.x, direction.y, direction.z);
    }

    public static void onDecay(LivingEntity entity) {
        StuckArrowSlots slots = entity.getData(ModAttachments.STUCK_ARROWS);
        if (slots.removeLoose()) {
            changed(entity, slots);
        } else {
            entity.setArrowCount(slots.size());
        }
    }

    private static ItemStack recoveredStack(AbstractArrow arrow) {
        boolean recoverable = switch (arrow.pickup) {
            case ALLOWED -> true;
            case DISALLOWED -> arrow.getOwner() != null && !(arrow.getOwner() instanceof Player);
            case CREATIVE_ONLY -> false;
        };
        if (!recoverable) return ItemStack.EMPTY;
        ItemStack stack = arrow.getPickupItemStackOrigin().copyWithCount(1);
        if (!(stack.getItem() instanceof TippedArrowItem)) stack.remove(DataComponents.POTION_CONTENTS);
        return stack;
    }

    private static void changed(LivingEntity entity, StuckArrowSlots slots) {
        entity.setArrowCount(slots.size());
        if (slots.isEmpty()) {
            entity.removeData(ModAttachments.STUCK_ARROWS);
        }
        sync(entity);
    }

    private static void sync(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload(entity));
    }

    private static StuckArrowsPayload payload(LivingEntity entity) {
        return new StuckArrowsPayload(entity.getId(), entity.hasData(ModAttachments.STUCK_ARROWS)
                ? List.copyOf(entity.getData(ModAttachments.STUCK_ARROWS).slots())
                : List.of());
    }

    public static boolean canPull(Player player, Entity target) {
        if (!(target instanceof LivingEntity living) || !living.isAlive() || !player.isAlive() || player.isSpectator()) return false;
        if (target == player) return true;
        if (!(target instanceof OwnableEntity ownable)) return false;
        return switch (target) {
            case TamableAnimal tamable -> tamable.isTame();
            case AbstractHorse horse -> horse.isTamed();
            default -> ownable.getOwnerUUID() != null;
        };
    }

    public static void pull(ServerPlayer player, int entityId, int slotIndex, int part) {
        if (!enabled()) return;
        Entity target = player.level().getEntity(entityId);
        if (!canPull(player, target)) return;
        if (target == player) {
            if (player.containerMenu != player.inventoryMenu) return;
        } else if (!player.canInteractWithEntity(target, REACH_PADDING) || !player.hasLineOfSight(target)) {
            return;
        }
        long now = player.level().getGameTime();
        Long last = LAST_REQUEST.get(player);
        if (last != null && now >= last && now - last < REQUEST_COOLDOWN_TICKS) return;
        LivingEntity living = (LivingEntity) target;
        if (!living.hasData(ModAttachments.STUCK_ARROWS)) return;
        StuckArrowSlots slots = living.getData(ModAttachments.STUCK_ARROWS);
        StuckArrowSlots.Slot slot = slots.get(slotIndex);
        if (slot == null) return;
        LAST_REQUEST.put(player, now);
        slots.remove(slotIndex);
        changed(living, slots);
        ServerLevel level = player.serverLevel();
        float chance = PULL_CHANCE[Math.clamp(part, HEAD, LIMB)];
        if (player.getRandom().nextFloat() < chance) {
            if (slot.recoverable()) {
                ItemStack stack = slot.stack().copy();
                if (!player.getInventory().add(stack)) player.drop(stack, false);
            }
            level.playSound(null, living.getX(), living.getY(0.5), living.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                    0.5F, 0.8F + player.getRandom().nextFloat() * 0.3F);
        } else {
            ItemStack shown = slot.recoverable() ? slot.stack() : new ItemStack(Items.ARROW);
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, shown), living.getX(), living.getY(0.6), living.getZ(),
                    6, living.getBbWidth() * 0.25, living.getBbHeight() * 0.2, living.getBbWidth() * 0.25, 0.05);
            level.playSound(null, living.getX(), living.getY(0.5), living.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS,
                    0.5F, 1.4F + player.getRandom().nextFloat() * 0.2F);
            living.hurt(level.damageSources().generic(), BREAK_DAMAGE);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !entity.hasData(ModAttachments.STUCK_ARROWS)) return;
        StuckArrowSlots slots = entity.getData(ModAttachments.STUCK_ARROWS);
        for (StuckArrowSlots.Slot slot : slots.slots()) {
            if (slot.recoverable()) entity.spawnAtLocation(slot.stack().copy());
        }
        slots.slots().clear();
        changed(entity, slots);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity living && living.hasData(ModAttachments.STUCK_ARROWS)
                && event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, payload(living));
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasData(ModAttachments.STUCK_ARROWS)) {
            PacketDistributor.sendToPlayer(player, payload(player));
        }
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasData(ModAttachments.STUCK_ARROWS)) {
            PacketDistributor.sendToPlayer(player, payload(player));
        }
    }
}
