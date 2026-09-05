package com.evandev.spicedcider.util;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class PetArmorUtil {

    private static final float REPAIR_FRACTION = 0.125F;

    private PetArmorUtil() {
    }

    public static boolean isPetArmor(ItemStack stack) {
        return stack.getItem() instanceof AnimalArmorItem;
    }

    public static boolean isEquestrian(ItemStack stack) {
        return stack.getItem() instanceof AnimalArmorItem armor
                && armor.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN;
    }

    public static boolean canAbsorb(LivingEntity pet, DamageSource source) {
        ItemStack armor = pet.getItemBySlot(EquipmentSlot.BODY);
        return isPetArmor(armor)
                && armor.isDamageableItem()
                && !source.is(DamageTypeTags.BYPASSES_WOLF_ARMOR);
    }

    public static void absorb(LivingEntity pet, float damageAmount) {
        ItemStack armor = pet.getItemBySlot(EquipmentSlot.BODY);
        int damageBefore = armor.getDamageValue();
        int maxDamage = armor.getMaxDamage();

        armor.hurtAndBreak(Mth.ceil(damageAmount), pet, EquipmentSlot.BODY);

        Crackiness.Level before = Crackiness.WOLF_ARMOR.byDamage(damageBefore, maxDamage);
        Crackiness.Level after = Crackiness.WOLF_ARMOR.byDamage(pet.getItemBySlot(EquipmentSlot.BODY));
        if (before != after) {
            pet.playSound(SoundEvents.WOLF_ARMOR_CRACK);
            if (pet.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, crackParticleItem(armor)),
                        pet.getX(),
                        pet.getY() + 1.0,
                        pet.getZ(),
                        20,
                        0.2,
                        0.1,
                        0.2,
                        0.1
                );
            }
        }
    }

    private static ItemStack crackParticleItem(ItemStack armor) {
        ItemStack[] candidates = repairIngredient(armor).getItems();
        return candidates.length > 0 ? candidates[0] : armor.copyWithCount(1);
    }

    public static Ingredient repairIngredient(ItemStack armor) {
        return armor.getItem() instanceof ArmorItem armorItem
                ? armorItem.getMaterial().value().repairIngredient().get()
                : Ingredient.EMPTY;
    }

    public static boolean isRepairMaterial(ItemStack armor, ItemStack held) {
        return !held.isEmpty() && isPetArmor(armor) && repairIngredient(armor).test(held);
    }

    public static void repair(LivingEntity pet, ItemStack held) {
        ItemStack armor = pet.getItemBySlot(EquipmentSlot.BODY);
        held.shrink(1);
        pet.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
        int restored = (int) (armor.getMaxDamage() * REPAIR_FRACTION);
        armor.setDamageValue(Math.max(0, armor.getDamageValue() - restored));
    }
}
