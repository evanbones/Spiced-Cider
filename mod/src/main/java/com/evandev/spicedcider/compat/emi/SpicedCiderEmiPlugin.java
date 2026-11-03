package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.Comparison;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.util.Set;

@EmiEntrypoint
public class SpicedCiderEmiPlugin implements EmiPlugin {
    private static final Set<String> DUPLICATE_INFO_NAMESPACES = Set.of("mynethersdelight", "exposure");

    @Override
    public void register(EmiRegistry registry) {
        if (SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.emiTappingCategory, false) && ModList.get().isLoaded("nomansland")) {
            TappingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("toil_and_trouble")) {
            BrewingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("etcetera")) {
            ChiselingEmiRecipes.register(registry);
        }
        CauldronMixingEmiRecipe.register(registry);
        LootEmiCategories.register(registry);
        registry.removeRecipes(recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.SMITHING);
        registry.removeRecipes(recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.INFO && recipe.getId() == null
                && !recipe.getInputs().isEmpty()
                && recipe.getInputs().stream().flatMap(input -> input.getEmiStacks().stream())
                .allMatch(stack -> DUPLICATE_INFO_NAMESPACES.contains(stack.getId().getNamespace())));
        AnvilSmithingEmiRecipe.register(registry);
        if (ModList.get().isLoaded("vanity")) {
            TrimStylingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("supplementaries")) {
            registry.setDefaultComparison(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("supplementaries", "cartographers_quill")), Comparison.compareComponents());
        }
    }
}
