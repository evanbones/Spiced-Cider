package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.create.BasinStirring;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.neoforge.NeoForgeEmiIngredient;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

public class LadleMixingEmiRecipe extends BasicEmiRecipe {
    private static final int WIDTH = 134;
    private static final int SLOT = 18;
    private static final int PER_ROW = 3;
    private static final ResourceLocation BASIN = ResourceLocation.fromNamespaceAndPath("create", "basin");

    private final List<EmiIngredient> ingredients;
    private final boolean heated;

    private LadleMixingEmiRecipe(EmiRecipeCategory category, ResourceLocation id, List<EmiIngredient> ingredients, List<EmiStack> outputs, boolean heated) {
        super(category, id, WIDTH, rows(ingredients.size()) * SLOT + (heated ? 12 : 0));
        this.ingredients = ingredients;
        this.heated = heated;
        this.inputs = ingredients;
        this.outputs = outputs;
    }

    private static int rows(int count) {
        return Math.max(1, (count + PER_ROW - 1) / PER_ROW);
    }

    public static void register(EmiRegistry registry) {
        EmiStack basin = EmiStack.of(BuiltInRegistries.ITEM.get(BASIN));
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "ladle_mixing"), basin);
        registry.addCategory(category);
        registry.addWorkstation(category, basin);
        registry.addWorkstation(category, EmiIngredient.of(BasinStirring.SPOONS));
        for (RecipeHolder<MixingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(AllRecipeTypes.MIXING.<RecipeInput, MixingRecipe>getType())) {
            MixingRecipe recipe = holder.value();
            if (!BasinStirring.handMixable(recipe)) continue;
            List<EmiIngredient> ingredients = new ArrayList<>();
            for (SizedFluidIngredient fluid : recipe.getFluidIngredients()) {
                ingredients.add(NeoForgeEmiIngredient.of(fluid));
            }
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredients.add(EmiIngredient.of(ingredient));
            }
            List<EmiStack> outputs = new ArrayList<>();
            for (ProcessingOutput output : recipe.getRollableResults()) {
                outputs.add(EmiStack.of(output.getStack()).setChance(output.getChance()));
            }
            for (FluidStack fluid : recipe.getFluidResults()) {
                outputs.add(NeoForgeEmiStack.of(fluid));
            }
            if (ingredients.isEmpty() || outputs.isEmpty()) continue;
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID,
                    "/ladle_mixing/" + holder.id().getNamespace() + "/" + holder.id().getPath());
            registry.addRecipe(new LadleMixingEmiRecipe(category, id, ingredients, outputs, recipe.getRequiredHeat() == HeatCondition.HEATED));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int rows = rows(this.ingredients.size());
        int middle = (rows * SLOT - SLOT) / 2;
        for (int i = 0; i < this.ingredients.size(); i++) {
            widgets.addSlot(this.ingredients.get(i), (i % PER_ROW) * SLOT, (i / PER_ROW) * SLOT);
        }
        widgets.addTexture(EmiTexture.EMPTY_ARROW, PER_ROW * SLOT + 6, middle + 1);
        int x = WIDTH - SLOT * Math.min(this.outputs.size(), 3);
        for (int i = 0; i < this.outputs.size(); i++) {
            widgets.addSlot(this.outputs.get(i), x + (i % 3) * SLOT, middle + (i / 3) * SLOT).recipeContext(this);
        }
        if (this.heated) {
            widgets.addText(Component.translatable("spicedcider.emi.ladle_mixing.heated"), 0, rows * SLOT + 3, 0xFF404040, false);
        }
    }
}
