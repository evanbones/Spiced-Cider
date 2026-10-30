package com.evandev.spicedcider.content.smithing;

import com.evandev.spicedcider.compat.quark.QuarkRunes;
import com.evandev.spicedcider.recipe.AnvilSmithingRecipe;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Optional;

public final class AnvilSmithing {

    public static final ResourceLocation TABLET_SMITHING = ResourceLocation.fromNamespaceAndPath("modestmagic", "tablet_smithing");

    public static ItemStack result(Level level, ItemStack left, ItemStack right) {
        if (left.isEmpty() || right.isEmpty()) return ItemStack.EMPTY;

        SmithingRecipeInput input = new SmithingRecipeInput(ItemStack.EMPTY, left, right);
        Optional<RecipeHolder<AnvilSmithingRecipe>> recipe = level.getRecipeManager().getRecipeFor(ModRecipeSerializers.ANVIL_SMITHING_TYPE.get(), input, level);
        if (recipe.isPresent()) return recipe.get().value().assemble(input, level.registryAccess());

        if (QuarkRunes.isRune(right)) return QuarkRunes.apply(left, right);

        SmithingRecipeInput tabletInput = new SmithingRecipeInput(right, left, ItemStack.EMPTY);
        for (RecipeHolder<SmithingRecipe> tablet : tabletRecipes(level)) {
            if (tablet.value().matches(tabletInput, level)) {
                return tablet.value().assemble(tabletInput, level.registryAccess());
            }
        }
        return ItemStack.EMPTY;
    }

    public static List<RecipeHolder<SmithingRecipe>> tabletRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream()
                .filter(holder -> isTablet(holder.value()))
                .toList();
    }

    public static Ingredient tabletIngredient(Recipe<?> recipe, String name) {
        if (!recipe.getClass().isRecord()) return Ingredient.EMPTY;
        for (RecordComponent component : recipe.getClass().getRecordComponents()) {
            if (!component.getName().equals(name)) continue;
            try {
                return component.getAccessor().invoke(recipe) instanceof Ingredient ingredient ? ingredient : Ingredient.EMPTY;
            } catch (ReflectiveOperationException e) {
                return Ingredient.EMPTY;
            }
        }
        return Ingredient.EMPTY;
    }

    public static boolean isTablet(Recipe<?> recipe) {
        return TABLET_SMITHING.equals(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer()));
    }
}
