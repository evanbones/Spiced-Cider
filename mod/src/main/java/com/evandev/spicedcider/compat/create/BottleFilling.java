package com.evandev.spicedcider.compat.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class BottleFilling {

    @Nullable
    public static FillingRecipe find(@Nullable Level level, ItemStack bottle, FluidStack fluid) {
        if (level == null || fluid.isEmpty() || !bottle.is(Items.GLASS_BOTTLE)) return null;
        SingleRecipeInput input = new SingleRecipeInput(bottle);
        for (RecipeHolder<FillingRecipe> holder : level.getRecipeManager().getAllRecipesFor(AllRecipeTypes.FILLING.<SingleRecipeInput, FillingRecipe>getType())) {
            FillingRecipe recipe = holder.value();
            if (recipe.getFluidIngredients().isEmpty() || !recipe.matches(input, level)) continue;
            if (!recipe.getRequiredFluid().ingredient().test(fluid)) continue;
            if (result(recipe).isEmpty()) continue;
            return recipe;
        }
        return null;
    }

    public static ItemStack result(FillingRecipe recipe) {
        for (ProcessingOutput output : recipe.getRollableResults()) {
            if (!output.getStack().isEmpty()) return output.getStack().copy();
        }
        return ItemStack.EMPTY;
    }
}
