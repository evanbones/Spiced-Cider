package com.evandev.spicedcider.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.Comparison;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

@EmiEntrypoint
public class SpicedCiderEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        if (ModList.get().isLoaded("nomansland")) {
            TappingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("toil_and_trouble")) {
            BrewingEmiRecipe.register(registry);
        }
        CauldronMixingEmiRecipe.register(registry);
        registry.removeRecipes(recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.SMITHING);
        AnvilSmithingEmiRecipe.register(registry);
        if (ModList.get().isLoaded("vanity")) {
            TrimStylingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("supplementaries")) {
            registry.setDefaultComparison(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("supplementaries", "cartographers_quill")), Comparison.compareComponents());
        }
    }
}
