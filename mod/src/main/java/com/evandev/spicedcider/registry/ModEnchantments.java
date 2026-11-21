package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> GUARD_BREAK = key("guard_break");
    public static final ResourceKey<Enchantment> BRACEWALK = key("bracewalk");
    public static final ResourceKey<Enchantment> SOULBOUND = key("soulbound");
    public static final ResourceKey<Enchantment> SEEKING = key("seeking");
    public static final ResourceKey<Enchantment> QUICK_DRAW = key("quick_draw");
    public static final ResourceKey<Enchantment> STRAIGHT_SHOT = key("straight_shot");
    public static final ResourceKey<Enchantment> DISPLACEMENT = key("displacement");
    public static final ResourceKey<Enchantment> CAVALIER = key("cavalier");

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name));
    }

    public static int level(Level level, ResourceKey<Enchantment> key, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        Optional<Holder.Reference<Enchantment>> holder = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(key);
        return holder.map(stack::getEnchantmentLevel).orElse(0);
    }

    public static int level(LivingEntity entity, ResourceKey<Enchantment> key, EquipmentSlot slot) {
        return level(entity.level(), key, entity.getItemBySlot(slot));
    }
}
