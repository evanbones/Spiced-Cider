package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

public class ElectrumArmorItem extends ArmorItem {
    private static final double SPEED_BONUS = 0.025;

    public ElectrumArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "electrum_speed_" + this.type.getName());
        return super.getDefaultAttributeModifiers(stack).withModifierAdded(
                Attributes.MOVEMENT_SPEED,
                new AttributeModifier(id, SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                EquipmentSlotGroup.bySlot(this.getEquipmentSlot())
        );
    }

    @Override
    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return true;
    }
}
