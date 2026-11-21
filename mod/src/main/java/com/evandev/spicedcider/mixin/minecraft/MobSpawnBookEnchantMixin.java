package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobSpawnBookEnchantMixin {

    @WrapOperation(method = "enchantSpawnedEquipment(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/util/RandomSource;FLnet/minecraft/world/DifficultyInstance;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItemFromProvider(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/util/RandomSource;)V"))
    private void spicedcider$skipHeldBooks(ItemStack stack, RegistryAccess registries, ResourceKey<EnchantmentProvider> provider, DifficultyInstance difficulty, RandomSource random, Operation<Void> original) {
        if (!stack.is(Items.BOOK)) {
            original.call(stack, registries, provider, difficulty, random);
        }
    }
}
