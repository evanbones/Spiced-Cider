package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.modest_meals.food.FoodValues;
import com.evandev.modest_meals.food.ingredient.IngredientProfile;
import com.evandev.modest_meals.tag.ModItemTags;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@IfModLoaded("modest_meals")
@Mixin(targets = "com.evandev.modest_meals.food.ingredient.IngredientProfileManager", remap = false)
public class IngredientProfileHealthFloorMixin {
    @ModifyReturnValue(method = "resolve", at = @At("RETURN"))
    private static Optional<IngredientProfile> spicedcider$keepEatingHealth(Optional<IngredientProfile> result, @Local(argsOnly = true) ItemStack stack) {
        if (result.isEmpty() || stack == null || stack.isEmpty() || stack.is(ModItemTags.INGREDIENT_MEALS)) return result;
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.mealIngredientsKeepHealth, false)) return result;
        IngredientProfile profile = result.get();
        float eaten = FoodValues.healthPoints(stack.copyWithCount(1));
        float inMeal = profile.healthOrZero();
        if (eaten <= inMeal) return result;
        int digest = profile.digestTicks().filter(ticks -> ticks > 0 && inMeal > 0)
                .map(ticks -> Math.round(ticks * eaten / inMeal))
                .orElse(Math.round(eaten * 30));
        return Optional.of(new IngredientProfile(Optional.of(eaten), profile.stamina(), Optional.of(digest),
                profile.eatSeconds(), profile.effect(), profile.potency(), profile.temporaryHealth(),
                profile.timeBonusSeconds(), profile.referenceNutrition()));
    }
}
