package com.evandev.spicedcider.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@IfModLoaded("farmersdelight")
@IfModLoaded("experience_obliterator")
@Mixin(value = CookingPotRecipe.class, remap = false)
public class CookingPotNoExperienceMixin {

    @ModifyReturnValue(method = "getExperience", at = @At("RETURN"))
    private float spicedcider$noExperience(float experience) {
        return 0.0F;
    }
}
