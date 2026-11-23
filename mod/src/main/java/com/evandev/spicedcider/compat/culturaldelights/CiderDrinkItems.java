package com.evandev.spicedcider.compat.culturaldelights;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import vectorwing.farmersdelight.common.item.DrinkableItem;

public class CiderDrinkItems {
    public static Item cider() {
        return new DrinkableItem(new Item.Properties()
                .craftRemainder(Items.GLASS_BOTTLE)
                .stacksTo(16)
                .food(new FoodProperties.Builder()
                        .nutrition(0)
                        .saturationModifier(0.8F)
                        .alwaysEdible()
                        .build()));
    }
}
