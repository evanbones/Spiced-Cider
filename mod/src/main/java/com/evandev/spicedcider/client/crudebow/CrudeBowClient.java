package com.evandev.spicedcider.client.crudebow;

import com.evandev.spicedcider.networking.CrudeArrowPayload;
import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CrudeBowClient {

    public static void registerItemProperties() {
        ItemProperties.register(ModItems.CRUDE_BOW.get(), ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, seed) -> {
            if (entity == null || entity.getUseItem() != stack) return 0.0F;
            return (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
        });
        ItemProperties.register(ModItems.CRUDE_BOW.get(), ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }

    public static void receive(CrudeArrowPayload payload) {
        if (Minecraft.getInstance().level == null) return;
        Entity entity = Minecraft.getInstance().level.getEntity(payload.entityId());
        if (entity != null) entity.setData(ModAttachments.CRUDE_ARROW, true);
    }
}
