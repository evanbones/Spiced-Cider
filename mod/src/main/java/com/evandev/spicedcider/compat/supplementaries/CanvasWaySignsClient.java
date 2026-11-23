package com.evandev.spicedcider.compat.supplementaries;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

public class CanvasWaySignsClient {
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (String prefix : CanvasWaySigns.prefixes()) {
            ResourceLocation type = CanvasWaySigns.typeId(prefix);
            event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(type.getNamespace(), "block/way_signs/" + type.getPath())));
        }
    }
}
