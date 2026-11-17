package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ConsumableBowlEvents {
    private static final String CONSUMABLE_ITEM = "vectorwing.farmersdelight.common.item.ConsumableItem";

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void dropDuplicateContainer(ModifyDefaultComponentsEvent event) {
        if (!ModList.get().isLoaded("farmersdelight")) return;
        Class<?> consumable;
        try {
            consumable = Class.forName(CONSUMABLE_ITEM);
        } catch (ClassNotFoundException e) {
            return;
        }
        for (Item item : BuiltInRegistries.ITEM) {
            if (!consumable.isInstance(item) || !item.hasCraftingRemainingItem()) continue;
            FoodProperties food = item.components().get(DataComponents.FOOD);
            if (food == null || food.usingConvertsTo().isEmpty()) continue;
            FoodProperties fixed = new FoodProperties(food.nutrition(), food.saturation(), food.canAlwaysEat(), food.eatSeconds(), Optional.empty(), food.effects());
            event.modify(item, builder -> builder.set(DataComponents.FOOD, fixed));
        }
    }
}
