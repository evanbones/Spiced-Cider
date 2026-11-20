package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.ItemAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MatchTool.class)
public class MatchToolModdedShearsMixin {

    @WrapOperation(
            method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ItemPredicate;test(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean spicedcider$moddedShearsCountAsShears(ItemPredicate predicate, ItemStack stack, Operation<Boolean> original) {
        if (original.call(predicate, stack)) return true;
        if (stack.is(Items.SHEARS) || !stack.canPerformAction(ItemAbilities.SHEARS_DIG)) return false;
        if (predicate.items().isEmpty() || !predicate.items().get().contains(Items.SHEARS.builtInRegistryHolder())) return false;
        return original.call(predicate, stack.transmuteCopy(Items.SHEARS));
    }
}
