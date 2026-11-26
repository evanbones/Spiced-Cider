package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.util.Set;

@EmiEntrypoint
public class SpicedCiderEmiPlugin implements EmiPlugin {
    private static final Set<String> DUPLICATE_INFO_NAMESPACES = Set.of("mynethersdelight", "exposure");
    private static final ResourceLocation WOODWORKS_SAWING = ResourceLocation.fromNamespaceAndPath("woodworks", "sawing");

    @Override
    public void register(EmiRegistry registry) {
        if (SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.emiTappingCategory, false) && ModList.get().isLoaded("nomansland")) {
            TappingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("create")) {
            BrewingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("etcetera")) {
            ChiselingEmiRecipes.register(registry);
        }
        if (ModList.get().isLoaded("clayworks")) {
            KilnBakingEmi.register(registry);
        }
        if (ModList.get().isLoaded("create")) {
            LadleMixingEmiRecipe.register(registry);
            BasinWashingEmiRecipes.register(registry);
        }
        CastingEmiRecipes.register(registry);
        StoneGenerationEmiRecipes.register(registry);
        if (ModList.get().isLoaded("modestmining")) {
            JavelinRepairEmiRecipes.register(registry);
        }
        if (ModList.get().isLoaded("horseman")) {
            ComponentTransferringEmiRecipes.register(registry);
        }
        if (ModList.get().isLoaded("slime_time")) {
            SlimeColorCraftingEmiRecipes.register(registry);
        }
        RepairMaterialTagEmiRecipes.register(registry);
        TanningEmiRecipe.register(registry);
        LootEmiCategories.register(registry);
        if (SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.emiHideSmithingCategory, false)) {
            registry.removeRecipes(recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.SMITHING);
        }
        registry.removeRecipes(recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.INFO && recipe.getId() == null
                && !recipe.getInputs().isEmpty()
                && recipe.getInputs().stream().flatMap(input -> input.getEmiStacks().stream())
                .allMatch(stack -> DUPLICATE_INFO_NAMESPACES.contains(stack.getId().getNamespace())));
        if (ReliableRemoverCompat.isLoaded()) {
            registry.removeRecipes(recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.GRINDING
                    && recipe.getInputs().stream().anyMatch(SpicedCiderEmiPlugin::isRemovedIngredient));
        }
        AnvilSmithingEmiRecipe.register(registry);
        if (ModList.get().isLoaded("vanity")) {
            TrimStylingEmiRecipe.register(registry);
        }
        if (ModList.get().isLoaded("farmersdelight")) {
            registry.addWorkstation(VanillaEmiRecipeCategories.CAMPFIRE_COOKING,
                    EmiStack.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("farmersdelight", "stove"))));
        }
        if (ModList.get().isLoaded("create") && ModList.get().isLoaded("woodworks")) {
            EmiStack saw = EmiStack.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("create", "mechanical_saw")));
            registry.addDeferredRecipes(consumer -> EmiRecipes.categories.stream()
                    .filter(category -> WOODWORKS_SAWING.equals(category.getId()))
                    .forEach(category -> registry.addWorkstation(category, saw)));
        }
        if (ModList.get().isLoaded("supplementaries")) {
            registry.setDefaultComparison(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("supplementaries", "cartographers_quill")), Comparison.compareComponents());
        }
    }

    private static boolean isRemovedIngredient(EmiIngredient ingredient) {
        var stacks = ingredient.getEmiStacks();
        return !stacks.isEmpty() && stacks.stream().allMatch(stack -> !stack.getItemStack().isEmpty()
                && ReliableRemoverCompat.isHiddenFromViewers(stack.getItemStack().getItem()));
    }
}
