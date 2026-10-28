package com.evandev.spicedcider.compat.bountifulfares;

import net.hecco.bountifulfares.definition.item.custom.TeaBottleItem;
import net.hecco.bountifulfares.registry.content.BFEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class LavenderTeaMugItem extends TeaBottleItem {

    public LavenderTeaMugItem() {
        super(List.of(new MobEffectInstance(MobEffects.BLINDNESS), new MobEffectInstance(MobEffects.DARKNESS)),
                new Item.Properties()
                        .stacksTo(16)
                        .food(new FoodProperties.Builder()
                                .nutrition(4)
                                .saturationModifier(1.0F)
                                .effect(() -> new MobEffectInstance(BFEffects.EBULLIENCE, 6000, 0, true, true), 1.0F)
                                .alwaysEdible()
                                .build()));
    }

    @Override
    public ArrayList<Holder<MobEffect>> getStatusEffectsToRemove() {
        ArrayList<Holder<MobEffect>> list = new ArrayList<>();
        list.add(MobEffects.BLINDNESS);
        list.add(MobEffects.DARKNESS);
        return list;
    }
}
