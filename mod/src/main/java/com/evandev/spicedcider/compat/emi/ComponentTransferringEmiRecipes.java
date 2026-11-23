package com.evandev.spicedcider.compat.emi;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public final class ComponentTransferringEmiRecipes {
    private static final ResourceLocation SERIALIZER = ResourceLocation.fromNamespaceAndPath("horseman", "component_transferring");

    public static void register(EmiRegistry registry) {
        RecipeSerializer<?> serializer = BuiltInRegistries.RECIPE_SERIALIZER.get(SERIALIZER);
        Minecraft minecraft = Minecraft.getInstance();
        if (serializer == null || minecraft.level == null) return;
        RegistryAccess access = minecraft.level.registryAccess();
        for (RecipeHolder<CraftingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            CraftingRecipe recipe = holder.value();
            if (recipe.getSerializer() != serializer) continue;
            Ingredient source = sourceIngredient(recipe);
            if (source == null) continue;
            List<EmiIngredient> others = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
            ItemStack result = recipe.getResultItem(access);
            ResourceLocation id = holder.id();
            for (ItemStack sourceStack : variants(source, access)) {
                List<EmiIngredient> inputs = new ArrayList<>();
                inputs.add(EmiStack.of(sourceStack));
                inputs.addAll(others);
                ItemStack output = result.copy();
                output.applyComponents(sourceStack.getComponents());
                String suffix = sourceStack.has(DataComponents.INSTRUMENT)
                        ? "/" + sourceStack.get(DataComponents.INSTRUMENT).unwrapKey().map(key -> key.location().getNamespace() + "/" + key.location().getPath()).orElse("unknown")
                        : "";
                ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath("spicedcider", "/component_transferring/" + id.getNamespace() + "/" + id.getPath() + suffix);
                registry.addRecipe(new EmiCraftingRecipe(inputs, EmiStack.of(output), recipeId, true));
            }
        }
    }

    private static List<ItemStack> variants(Ingredient source, RegistryAccess access) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack stack : source.getItems()) {
            if (stack.getItem() instanceof InstrumentItem && stack.is(Items.GOAT_HORN) && !stack.has(DataComponents.INSTRUMENT)) {
                for (Holder<Instrument> instrument : access.registryOrThrow(Registries.INSTRUMENT).getTagOrEmpty(InstrumentTags.GOAT_HORNS)) {
                    ItemStack variant = stack.copy();
                    variant.set(DataComponents.INSTRUMENT, instrument);
                    result.add(variant);
                }
            } else {
                result.add(stack);
            }
        }
        return result;
    }

    private static Ingredient sourceIngredient(CraftingRecipe recipe) {
        try {
            return (Ingredient) recipe.getClass().getMethod("getSourceIngredient").invoke(recipe);
        } catch (ReflectiveOperationException | ClassCastException e) {
            return null;
        }
    }
}
