package com.evandev.spicedcider.content.casting;

import com.evandev.spicedcider.registry.ModRecipeSerializers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class Casting {
    public static List<RecipeHolder<CrucibleMeltingRecipe>> meltingRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CRUCIBLE_MELTING_TYPE.get()).stream()
                .sorted(Comparator.comparingInt((RecipeHolder<CrucibleMeltingRecipe> holder) -> holder.value().ingredients().size()).reversed())
                .toList();
    }

    public static int fluidValue(Level level, Ingredient ingredient, FluidStack fluid) {
        for (RecipeHolder<CrucibleMeltingRecipe> holder : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CRUCIBLE_MELTING_TYPE.get())) {
            CrucibleMeltingRecipe recipe = holder.value();
            if (recipe.ingredients().size() != 1 || !FluidStack.isSameFluidSameComponents(recipe.result(), fluid)) {
                continue;
            }
            Ingredient melted = recipe.ingredients().getFirst();
            for (ItemStack stack : ingredient.getItems()) {
                if (melted.test(stack)) {
                    return recipe.result().getAmount();
                }
            }
        }
        return 0;
    }

    public static boolean isMeltable(Level level, ItemStack stack) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CRUCIBLE_MELTING_TYPE.get()).stream()
                .anyMatch(holder -> holder.value().uses(stack));
    }

    public static boolean castingRecipeExists(Level level, FluidStack fluid) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.MOULD_CASTING_TYPE.get()).stream()
                .anyMatch(holder -> holder.value().fluid().ingredient().test(fluid))
                || level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.BASIN_CASTING_TYPE.get()).stream()
                .anyMatch(holder -> holder.value().accepts(fluid));
    }

    @Nullable
    public static RecipeHolder<MouldCastingRecipe> castingRecipe(Level level, MouldShape shape, FluidStack fluid) {
        if (fluid.isEmpty()) {
            return null;
        }
        for (RecipeHolder<MouldCastingRecipe> holder : level.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.MOULD_CASTING_TYPE.get())) {
            if (holder.value().accepts(shape, fluid)) {
                return holder;
            }
        }
        return null;
    }
}
