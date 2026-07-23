package com.evandev.spicedcider.content.item;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

public class MischiefArmorItem extends ArmorItem {

    public MischiefArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        ItemAttributeModifiers base = super.getDefaultAttributeModifiers(stack);
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "blast_proof_" + this.type.getName());
        float reduction = switch (this.type) {
            case HELMET, LEGGINGS -> 0.25F;
            case CHESTPLATE -> 0.30F;
            case BOOTS -> 0.20F;
            default -> 0.0F;
        };
        return base.withModifierAdded(
                ModAttributes.EXPLOSIVE_DAMAGE_REDUCTION,
                new AttributeModifier(id, reduction, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.bySlot(this.getEquipmentSlot())
        );
    }

    @Override
    public boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
        return true;
    }
}
