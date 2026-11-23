package com.evandev.spicedcider.content.effect;

import com.evandev.spicedcider.compat.personality.PersonalitySitting;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ComfortEffect extends MobEffect {
    private static final ResourceLocation HEALTH_NO_REGEN = ResourceLocation.fromNamespaceAndPath("modest_meals", "health_no_regen");

    public ComfortEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xDDF4F5);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide() || entity.getHealth() >= entity.getMaxHealth()) return true;
        if (entity.hasEffect(MobEffects.REGENERATION) || hasNoRegen(entity)) return true;
        if (entity.tickCount % interval(entity) == 0) entity.heal(1.0F);
        return true;
    }

    private static int interval(LivingEntity entity) {
        if (entity instanceof Player player && isResting(player)) {
            return Math.max(1, SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.comfortRestingHealInterval, 40));
        }
        return Math.max(1, SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.comfortHealInterval, 80));
    }

    public static boolean isResting(Player player) {
        return player.isSleeping() || PersonalitySitting.isSittingOrSeated(player);
    }

    private static boolean hasNoRegen(LivingEntity entity) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(HEALTH_NO_REGEN).map(entity::hasEffect).orElse(false);
    }
}
