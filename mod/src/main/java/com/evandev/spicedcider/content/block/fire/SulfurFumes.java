package com.evandev.spicedcider.content.block.fire;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.List;

public class SulfurFumes {
    public static final int FIRE_NAUSEA_TICKS = 100;
    public static final int FOOD_NAUSEA_TICKS = 160;

    public static void expose(Entity entity) {
        if (entity.level().isClientSide || !(entity instanceof LivingEntity living) || living.fireImmune()) return;
        MobEffectInstance current = living.getEffect(MobEffects.CONFUSION);
        if (current == null || current.getDuration() < FIRE_NAUSEA_TICKS / 2) {
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, FIRE_NAUSEA_TICKS, 0, false, true));
        }
    }

    public static ItemStack taint(ItemStack stack) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null) return stack;
        for (FoodProperties.PossibleEffect effect : food.effects()) {
            if (effect.effect().is(MobEffects.CONFUSION)) return stack;
        }
        List<FoodProperties.PossibleEffect> effects = new ArrayList<>(food.effects());
        effects.add(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(MobEffects.CONFUSION, FOOD_NAUSEA_TICKS, 0), 1.0F));
        stack.set(DataComponents.FOOD, new FoodProperties(food.nutrition(), food.saturation(), food.canAlwaysEat(), food.eatSeconds(), food.usingConvertsTo(), effects));
        return stack;
    }
}
