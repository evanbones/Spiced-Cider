package com.evandev.spicedcider.compat.vanillabackport;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.neoforged.fml.ModList;

public final class VanillaBackportCompat {

    public static final String MOD_ID = "vanillabackport";

    private static Boolean loaded;

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    public static Item createLance(Tier tier, ResourceLocation reachId, Item.Properties properties) {
        if (isLoaded()) {
            return VanillaBackportBridge.createLance(tier, properties);
        }
        return new SwordItem(tier, properties.attributes(SwordItem.createAttributes(tier, 4, -3.0F)
                .withModifierAdded(Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(reachId, 1.5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)));
    }
}
