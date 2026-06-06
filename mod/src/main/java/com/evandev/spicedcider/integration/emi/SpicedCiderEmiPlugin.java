package com.evandev.spicedcider.integration.emi;

import cc.cassian.item_descriptions.client.descriptions.ItemDescriptions;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.recipe.WorkstoneRecipe;
import com.evandev.spicedcider.registry.ModRecipeTypes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@EmiEntrypoint
public class SpicedCiderEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(SpicedCiderCategories.WORKSTONE);
        registry.addWorkstation(SpicedCiderCategories.WORKSTONE, SpicedCiderWorkstations.WORKSTONE);

        for (RecipeHolder<WorkstoneRecipe> recipeHolder : registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.WORKSTONE.get())) {
            WorkstoneRecipe recipe = recipeHolder.value();
            var emiOutputs = recipe.getResults().stream()
                    .map(chanceResult -> EmiStack.of(chanceResult.stack()).setChance(chanceResult.chance()))
                    .toList();

            registry.addRecipe(new WorkstoneEmiRecipe(
                    recipeHolder.id(),
                    EmiIngredient.of(recipe.getTool()),
                    EmiIngredient.of(recipe.getIngredients().getFirst()),
                    emiOutputs
            ));
        }

        for (Item item : BuiltInRegistries.ITEM) {
            var stack = item.getDefaultInstance();
            var key = ItemDescriptions.findLoreKey(stack);

            if (key != null && key.hasTranslation()) {
                registry.addRecipe(new EmiInfoRecipe(
                        List.of(EmiStack.of(stack)),
                        List.of(key.toText()),
                        ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/emi_info_" + BuiltInRegistries.ITEM.getKey(item).getPath())
                ));
            }
        }
    }
}