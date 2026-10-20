package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class DrinkIntoxicationEvents {
    private static final ResourceLocation INTOXICATION = ResourceLocation.fromNamespaceAndPath("culturaldelights", "intoxication");
    private static final int DURATION = 1200;
    private static final List<String> DRINKS = List.of(
            "bountifulfares:plum_cider_jar",
            "bountifulfares:hoary_cider_jar",
            "bountifulfares:elderberry_wine_bottle",
            "nomansland:maple_mead_bottle"
    );

    @SubscribeEvent
    public static void addIntoxication(ModifyDefaultComponentsEvent event) {
        Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(INTOXICATION);
        if (effect.isEmpty()) return;
        for (String id : DRINKS) {
            BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id)).ifPresent(item -> {
                FoodProperties food = item.components().get(DataComponents.FOOD);
                if (food == null) return;
                List<FoodProperties.PossibleEffect> effects = new ArrayList<>(food.effects());
                effects.add(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(effect.get(), DURATION, 0), 1.0F));
                FoodProperties drink = new FoodProperties(food.nutrition(), food.saturation(), food.canAlwaysEat(), food.eatSeconds(), food.usingConvertsTo(), List.copyOf(effects));
                event.modify(item, builder -> builder.set(DataComponents.FOOD, drink));
            });
        }
    }
}
