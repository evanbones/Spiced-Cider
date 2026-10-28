package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.mixing.CauldronMixing;
import com.evandev.spicedcider.content.mixing.CauldronMixingRecipe;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class CauldronMixingEmiRecipe extends BasicEmiRecipe {
    private static final int WIDTH = 134;
    private static final int SLOT = 18;
    private static final int PER_ROW = 3;

    private final EmiStack fluid;
    private final List<EmiIngredient> items;
    private final boolean heated;

    private CauldronMixingEmiRecipe(EmiRecipeCategory category, ResourceLocation id, CauldronMixingRecipe recipe) {
        super(category, id, WIDTH, Math.max(1, (recipe.ingredients().size() + PER_ROW - 1) / PER_ROW) * SLOT + (recipe.heated() ? 12 : 0));
        this.fluid = recipe.fluid() == Fluids.EMPTY ? EmiStack.of(Items.CAULDRON) : EmiStack.of(recipe.fluid(), 1000);
        this.items = recipe.ingredients().stream().map(EmiIngredient::of).toList();
        this.heated = recipe.heated();
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(this.fluid);
        inputs.addAll(this.items);
        this.inputs = inputs;
        List<EmiStack> outputs = new ArrayList<>();
        if (!recipe.result().isEmpty()) {
            outputs.add(EmiStack.of(recipe.result()));
        }
        recipe.resultFluid().ifPresent(result -> outputs.add(EmiStack.of(result, 1000)));
        this.outputs = outputs;
    }

    public static void register(EmiRegistry registry) {
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cauldron_mixing"), EmiStack.of(Items.CAULDRON));
        registry.addCategory(category);
        registry.addWorkstation(category, EmiStack.of(Items.CAULDRON));
        registry.addWorkstation(category, EmiIngredient.of(CauldronMixing.SPOONS));
        for (RecipeHolder<CauldronMixingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.CAULDRON_MIXING_TYPE.get())) {
            registry.addRecipe(new CauldronMixingEmiRecipe(category, holder.id(), holder.value()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int rows = Math.max(1, (this.items.size() + PER_ROW - 1) / PER_ROW);
        int middle = (rows * SLOT - SLOT) / 2;
        widgets.addSlot(this.fluid, 0, middle);
        widgets.addTexture(EmiTexture.PLUS, 21, middle + 4);
        for (int i = 0; i < this.items.size(); i++) {
            widgets.addSlot(this.items.get(i), 34 + (i % PER_ROW) * SLOT, (i / PER_ROW) * SLOT);
        }
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 34 + PER_ROW * SLOT + 2, middle + 1);
        int x = WIDTH - SLOT * this.outputs.size();
        for (int i = 0; i < this.outputs.size(); i++) {
            widgets.addSlot(this.outputs.get(i), x + i * SLOT, middle).recipeContext(this);
        }
        if (this.heated) {
            widgets.addText(Component.translatable("spicedcider.emi.cauldron_mixing.heated"), 0, rows * SLOT + 3, 0xFF404040, false);
        }
    }
}
