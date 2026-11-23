package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.modest_meals.trait.FoodTrait;
import com.evandev.modest_meals.trait.impl.StaminaAdditionTrait;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("modest_meals")
@Mixin(targets = "com.evandev.modest_meals.food.FoodValues", remap = false)
public class FoodValuesNoStaminaMixin {
    @ModifyReturnValue(method = "effectiveTraits(Lnet/minecraft/world/item/ItemStack;I)Ljava/util/List;", at = @At("RETURN"))
    private static List<FoodTrait> spicedcider$noFoodStamina(List<FoodTrait> traits) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.noFoodStamina, false)) return traits;
        if (traits.stream().noneMatch(trait -> trait instanceof StaminaAdditionTrait)) return traits;
        return traits.stream().filter(trait -> !(trait instanceof StaminaAdditionTrait)).toList();
    }
}
