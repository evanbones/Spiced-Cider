package com.evandev.spicedcider.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.IdentityHashMap;
import java.util.Map;

public final class PetArmorDurability {

    private static final int MIN_FACTOR = 4;
    private static Map<ArmorMaterial, Integer> factors;

    private PetArmorDurability() {
    }

    public static int bodyDurabilityFor(Holder<ArmorMaterial> material) {
        return ArmorItem.Type.BODY.getDurability(factorFor(material));
    }

    public static int factorFor(Holder<ArmorMaterial> material) {
        Integer derived = derivedFactors().get(material.value());
        return derived != null ? derived : fallbackFactor(material);
    }

    private static Map<ArmorMaterial, Integer> derivedFactors() {
        if (factors == null) {
            factors = deriveFactors();
        }
        return factors;
    }

    private static Map<ArmorMaterial, Integer> deriveFactors() {
        Map<ArmorMaterial, Integer> derived = new IdentityHashMap<>();
        Map<ArmorMaterial, Integer> sampleWeight = new IdentityHashMap<>();

        for (Item item : BuiltInRegistries.ITEM) {
            if (!(item instanceof ArmorItem armor)) continue;

            int maxDamage = item.components().getOrDefault(DataComponents.MAX_DAMAGE, 0);
            if (maxDamage <= 0) continue;

            int base = armor.getType().getDurability(1);
            if (base <= 0) continue;

            ArmorMaterial material = armor.getMaterial().value();
            if (base <= sampleWeight.getOrDefault(material, 0)) continue;

            sampleWeight.put(material, base);
            derived.put(material, Math.max(MIN_FACTOR, Math.round((float) maxDamage / base)));
        }

        return derived;
    }

    private static int fallbackFactor(Holder<ArmorMaterial> material) {
        return Math.max(MIN_FACTOR, material.value().getDefense(ArmorItem.Type.BODY));
    }
}
