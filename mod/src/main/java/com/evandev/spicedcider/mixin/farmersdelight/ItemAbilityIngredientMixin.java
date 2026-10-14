package com.evandev.spicedcider.mixin.farmersdelight;

import com.evandev.spicedcider.util.ItemAbilityItemsCache;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.stream.Stream;

@IfModLoaded("farmersdelight")
@Mixin(targets = "vectorwing.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient", remap = false)
public class ItemAbilityIngredientMixin {
    @Shadow
    @Final
    protected ItemAbility itemAbility;

    @WrapMethod(method = "getItems")
    private Stream<ItemStack> spicedcider$cachedItems(Operation<Stream<ItemStack>> original) {
        return ItemAbilityItemsCache.items(itemAbility).stream().map(ItemStack::new);
    }
}
