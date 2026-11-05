package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

final class KilnBakingEmi {
    private static final ResourceLocation BAKING = ResourceLocation.fromNamespaceAndPath("clayworks", "baking");

    static void register(EmiRegistry registry) {
        RecipeType<?> type = BuiltInRegistries.RECIPE_TYPE.get(BAKING);
        Item kiln = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("clayworks", "kiln"));
        if (type == null || kiln == Items.AIR) return;
        EmiStack workstation = EmiStack.of(kiln);
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "baking"), workstation);
        registry.addCategory(category);
        registry.addWorkstation(category, workstation);
        for (RecipeHolder<?> holder : registry.getRecipeManager().getRecipes()) {
            if (holder.value().getType() == type && holder.value() instanceof AbstractCookingRecipe recipe) {
                registry.addRecipe(new EmiCookingRecipe(recipe, category, 2, false));
            }
        }
        registry.removeRecipes(recipe -> recipe.getCategory() != category && BAKING.equals(recipe.getCategory().getId()));
    }
}
