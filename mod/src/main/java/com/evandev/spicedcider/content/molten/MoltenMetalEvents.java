package com.evandev.spicedcider.content.molten;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class MoltenMetalEvents {
    @SubscribeEvent
    public static void burnInMoltenMetal(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide || !entity.isInFluidType()) return;
        if (entity.isInFluidType((type, height) -> type instanceof MoltenFluidType)) entity.lavaHurt();
    }
}
