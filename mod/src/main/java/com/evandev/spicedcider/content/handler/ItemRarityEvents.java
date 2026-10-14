package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.Map;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ItemRarityEvents {
    private static final Map<String, Rarity> RARITIES = Map.of(
            "caverns_and_chasms:monocle", Rarity.UNCOMMON,
            "caverns_and_chasms:unicorn_horn", Rarity.UNCOMMON
    );

    @SubscribeEvent
    public static void setRarities(ModifyDefaultComponentsEvent event) {
        RARITIES.forEach((id, rarity) -> BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id))
                .ifPresent(item -> event.modify(item, builder -> builder.set(DataComponents.RARITY, rarity))));
    }
}
