package com.evandev.spicedcider.mixin.create;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.AllRecipeTypes;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(AllRecipeTypes.class)
public class AllRecipeTypesGappedShapedMixin {

    @ModifyReturnValue(method = "shouldIgnoreInAutomation", at = @At("RETURN"))
    private static boolean spicedcider$ignoreGappedShapedRecipes(boolean original, RecipeHolder<?> recipe) {
        if (original || !(recipe.value() instanceof CraftingRecipe crafting) || crafting instanceof ShapedRecipe) return original;
        for (Ingredient ingredient : crafting.getIngredients()) {
            if (ingredient.isEmpty()) return true;
        }
        return false;
    }
}
