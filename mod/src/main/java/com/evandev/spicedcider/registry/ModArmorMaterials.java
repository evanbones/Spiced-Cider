package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, SpicedCider.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MISCHIEF = ARMOR_MATERIALS.register("mischief", () -> new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.BOOTS, 2,
                    ArmorItem.Type.LEGGINGS, 5,
                    ArmorItem.Type.CHESTPLATE, 6,
                    ArmorItem.Type.HELMET, 2
            ),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            () -> Ingredient.of(ModItems.BLAST_PROOF_PLATING.get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "mischief"))),
            1.0F,
            0.0F
    ));
}
