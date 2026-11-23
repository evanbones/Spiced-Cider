package com.evandev.spicedcider.compat.modestmeals;

import com.evandev.modest_meals.food.FoodEffect;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class NourishmentDuration {
    private static final ResourceLocation NOURISHMENT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "nourishment");

    public static int percent() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.nourishmentDurationPercent, 100);
    }

    public static MobEffectInstance scale(MobEffectInstance effect, int percent) {
        if (effect.isInfiniteDuration()) return effect;
        int duration = Math.max(1, effect.getDuration() * percent / 100);
        return new MobEffectInstance(effect.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon());
    }

    public static List<FoodEffect> scaleFoodEffects(List<FoodEffect> effects) {
        int percent = percent();
        if (percent == 100 || effects.stream().noneMatch(food -> food.effect().getEffect().is(NOURISHMENT))) return effects;
        return effects.stream()
                .map(food -> food.effect().getEffect().is(NOURISHMENT) ? new FoodEffect(scale(food.effect(), percent), food.probability()) : food)
                .toList();
    }
}
