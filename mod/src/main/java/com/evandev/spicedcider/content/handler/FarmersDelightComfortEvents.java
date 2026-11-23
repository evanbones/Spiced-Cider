package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModMobEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class FarmersDelightComfortEvents {
    private static final ResourceLocation FARMERS_DELIGHT_COMFORT = ResourceLocation.fromNamespaceAndPath("farmersdelight", "comfort");

    @SubscribeEvent
    public static void replaceFarmersDelightComfort(ModifyDefaultComponentsEvent event) {
        if (!SpicedCiderConfig.startupOr(SpicedCiderConfig.STARTUP.replaceFarmersDelightComfort, false)) return;
        if (!BuiltInRegistries.MOB_EFFECT.containsKey(FARMERS_DELIGHT_COMFORT)) return;
        for (Item item : BuiltInRegistries.ITEM) {
            FoodProperties food = item.components().get(DataComponents.FOOD);
            if (food == null || food.effects().stream().noneMatch(possible -> possible.effect().getEffect().is(FARMERS_DELIGHT_COMFORT))) continue;
            List<FoodProperties.PossibleEffect> effects = food.effects().stream().map(possible -> {
                MobEffectInstance effect = possible.effect();
                if (!effect.getEffect().is(FARMERS_DELIGHT_COMFORT)) return possible;
                return new FoodProperties.PossibleEffect(() -> new MobEffectInstance(ModMobEffects.COMFORT, effect.getDuration(), 0, effect.isAmbient(), effect.isVisible(), effect.showIcon()), possible.probability());
            }).toList();
            FoodProperties replaced = new FoodProperties(food.nutrition(), food.saturation(), food.canAlwaysEat(), food.eatSeconds(), food.usingConvertsTo(), effects);
            event.modify(item, builder -> builder.set(DataComponents.FOOD, replaced));
        }
    }
}
