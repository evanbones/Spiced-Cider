package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.recipe.DryingRecipe;
import com.evandev.spicedcider.registry.ModItems;
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
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class TanningEmiRecipe extends BasicEmiRecipe {
    private static final int WIDTH = 116;
    private static final int HEIGHT = 26;

    private final EmiIngredient hide;
    private final EmiIngredient agent;
    private final int time;

    private TanningEmiRecipe(EmiRecipeCategory category, RecipeHolder<DryingRecipe> holder) {
        super(category, holder.id(), WIDTH, HEIGHT);
        DryingRecipe recipe = holder.value();
        this.hide = EmiIngredient.of(recipe.ingredient(), recipe.count());
        this.agent = recipe.agent().map(EmiIngredient::of).orElse(EmiStack.EMPTY);
        this.time = recipe.time();
        this.inputs = agent.isEmpty() ? List.of(hide) : List.of(hide, agent);
        this.catalysts = List.of(EmiStack.of(ModItems.TANNING_RACK.get()));
        this.outputs = List.of(EmiStack.of(recipe.result()));
    }

    public static void register(EmiRegistry registry) {
        EmiStack rack = EmiStack.of(ModItems.TANNING_RACK.get());
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "tanning"), rack);
        registry.addCategory(category);
        registry.addWorkstation(category, rack);
        for (RecipeHolder<DryingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.DRYING_TYPE.get())) {
            registry.addRecipe(new TanningEmiRecipe(category, holder));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(hide, 0, 4);
        if (!agent.isEmpty()) {
            widgets.addTexture(EmiTexture.PLUS, 21, 8);
            widgets.addSlot(agent, 34, 4);
        }
        widgets.addFillingArrow(58, 5, time * 50)
                .tooltipText(List.of(Component.translatable("emi.spicedcider.tanning.time", time / 20)));
        widgets.addSlot(outputs.getFirst(), WIDTH - 26, 0).large(true).recipeContext(this);
    }
}
