package com.evandev.spicedcider.content.handler;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class BucketCapabilities {

    public static void register(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BucketItem bucket && bucket.content != Fluids.EMPTY
                    && !event.isItemRegistered(Capabilities.FluidHandler.ITEM, item)) {
                event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidBucketWrapper(stack), item);
            }
        }
    }
}
