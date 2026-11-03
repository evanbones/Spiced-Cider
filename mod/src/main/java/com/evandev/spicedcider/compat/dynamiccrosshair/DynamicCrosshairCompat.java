package com.evandev.spicedcider.compat.dynamiccrosshair;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

public class DynamicCrosshairCompat {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(InterModEnqueueEvent.class, event ->
                InterModComms.sendTo("dynamiccrosshair", "register_api", SpicedCiderCrosshairApi::new));
    }
}
