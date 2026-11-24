package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class UnbreakableItemEvents {

    @SubscribeEvent
    public static void removeDurability(ModifyDefaultComponentsEvent event) {
        List<? extends String> ids = SpicedCiderConfig.startupOr(SpicedCiderConfig.STARTUP.unbreakableItems, List.of());
        for (String id : ids) {
            ResourceLocation key = ResourceLocation.tryParse(id);
            if (key == null) continue;
            BuiltInRegistries.ITEM.getOptional(key).ifPresent(item -> event.modify(item, builder -> builder
                    .remove(DataComponents.MAX_DAMAGE)
                    .remove(DataComponents.DAMAGE)));
        }
    }
}
