package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.item.CleaverOfBeheadingItem;
import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModAttributes;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class MischiefEvents {

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        Level level = player.level();

        if (!(target instanceof LivingEntity)) return;
        if (!(player.getMainHandItem().getItem() instanceof CleaverOfBeheadingItem)) return;

        float attackStrength = player.getAttackStrengthScale(0.5F);
        if (attackStrength <= 0.9F) return;
        if (player.isSprinting() || !player.onGround() || (player.walkDist - player.walkDistO) >= player.getSpeed())
            return;

        boolean shouldCrit = player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() && !player.isInWater()
                && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger();
        CriticalHitEvent critEvent = CommonHooks.fireCriticalHit(player, target, shouldCrit, shouldCrit ? 1.5F : 1.0F);
        if (critEvent.isCriticalHit()) return;

        target.setData(ModAttachments.NO_KNOCKBACK, true);

        float knockback = (float) player.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        AABB targetBox = target.getBoundingBox().inflate(1.5D, 0.25D, 1.5D);
        AABB shockwaveBox = new AABB(targetBox.minX, targetBox.minY, targetBox.minZ, targetBox.maxX, targetBox.minY + 3.25D, targetBox.maxZ);
        for (LivingEntity pushed : level.getEntitiesOfClass(LivingEntity.class, shockwaveBox)) {
            if (pushed != player && pushed != target && !player.isAlliedTo(pushed) && (!(pushed instanceof ArmorStand armorStand) || !armorStand.isMarker())) {
                pushed.knockback(0.4F + (knockback * 0.5F), Mth.sin(player.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(player.getYRot() * ((float) Math.PI / 180F)));
                if (pushed.isAffectedByPotions() && !pushed.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    pushed.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2));
                }
            }
        }

        if (!level.isClientSide) {
            BlockPos.MutableBlockPos checkingPos = new BlockPos.MutableBlockPos();
            RandomSource random = level.getRandom();
            for (int i = 0; i < 100; i++) {
                double x = shockwaveBox.minX + (random.nextDouble() * (shockwaveBox.maxX - shockwaveBox.minX));
                double z = shockwaveBox.minZ + (random.nextDouble() * (shockwaveBox.maxZ - shockwaveBox.minZ));
                int minY = Mth.floor(shockwaveBox.minY);
                for (int y = minY; y < Mth.floor(shockwaveBox.maxY); y++) {
                    checkingPos.set(Mth.floor(x), y, Mth.floor(z));
                    if (!isEmptySpace(level, checkingPos)) {
                        checkingPos.move(Direction.UP);
                        if (isEmptySpace(level, checkingPos)) {
                            checkingPos.move(Direction.DOWN);
                            BlockState state = level.getBlockState(checkingPos);
                            ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(checkingPos), x, y + state.getShape(level, checkingPos).max(Direction.Axis.Y), z, 0, 0.0D, 0.0D, 0.0D, 0.15D);
                            break;
                        }
                    }
                }
            }
            double xAngle = -Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
            double zAngle = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
            ((ServerLevel) level).sendParticles(ModParticleTypes.CLEAVER_SWEEP.get(), player.getX() + xAngle, player.getY(0.5D), player.getZ() + zAngle, 0, xAngle, 0.0D, zAngle, 0.0D);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
    }

    private static boolean isEmptySpace(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).isAir() || !level.getFluidState(pos).isEmpty();
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        if (event.getEntity().getData(ModAttachments.NO_KNOCKBACK)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity living && living.getData(ModAttachments.NO_KNOCKBACK)) {
            living.setData(ModAttachments.NO_KNOCKBACK, false);
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if (!event.getSource().is(DamageTypeTags.IS_EXPLOSION)) return;

        Holder<Enchantment> blastProtection = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.BLAST_PROTECTION);

        double decrease = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = target.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            List<ItemAttributeModifiers.Entry> modifiers = stack.getAttributeModifiers().modifiers();
            boolean hasReduction = false;
            for (ItemAttributeModifiers.Entry entry : modifiers) {
                if (entry.slot().test(slot) && entry.attribute().is(ModAttributes.EXPLOSIVE_DAMAGE_REDUCTION.getKey())) {
                    decrease += entry.modifier().amount();
                    hasReduction = true;
                }
            }

            if (hasReduction) {
                int blastProtLevel = EnchantmentHelper.getItemEnchantmentLevel(blastProtection, stack);
                stack.hurtAndBreak(22 - blastProtLevel * 8, target, slot);
            }
        }

        if (decrease != 0) {
            event.setNewDamage(event.getNewDamage() - (float) (event.getNewDamage() * decrease));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getHealth() > 0.0F) return;

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;
        if (!livingAttacker.getMainHandItem().is(ModItems.CLEAVER_OF_BEHEADING.get())) return;

        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        head.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
        player.spawnAtLocation(head);
    }
}
