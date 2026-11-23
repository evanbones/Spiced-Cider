package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.modest_meals.food.FoodEffect;
import com.evandev.spicedcider.compat.modestmeals.NourishmentDuration;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("modest_meals")
@IfModLoaded("farmersdelight")
@Mixin(targets = "com.evandev.modest_meals.compat.farmers_delight.FarmersDelightSoupEffects", remap = false)
public class NourishmentSoupTooltipMixin {
    @ModifyReturnValue(method = "get", at = @At("RETURN"))
    private static List<FoodEffect> spicedcider$shorterNourishment(List<FoodEffect> effects) {
        return NourishmentDuration.scaleFoodEffects(effects);
    }
}
