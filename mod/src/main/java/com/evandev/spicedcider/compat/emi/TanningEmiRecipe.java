package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.TanningRackBlock;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.registry.ModTags;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class TanningEmiRecipe extends BasicEmiRecipe {
    private static final int WIDTH = 116;
    private static final int HEIGHT = 26;

    private final EmiIngredient hide;
    private final EmiIngredient agent;

    private TanningEmiRecipe(EmiRecipeCategory category, String path, TagKey<Item> hides, int leather) {
        super(category, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/tanning/" + path), WIDTH, HEIGHT);
        this.hide = EmiIngredient.of(hides);
        this.agent = EmiIngredient.of(ModTags.Items.TANNING_AGENTS);
        this.inputs = List.of(hide, agent);
        this.catalysts = List.of(EmiStack.of(ModItems.TANNING_RACK.get()));
        this.outputs = List.of(EmiStack.of(Items.LEATHER, leather));
    }

    public static void register(EmiRegistry registry) {
        EmiStack rack = EmiStack.of(ModItems.TANNING_RACK.get());
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "tanning"), rack);
        registry.addCategory(category);
        registry.addWorkstation(category, rack);
        registry.addRecipe(new TanningEmiRecipe(category, "leather", ModTags.Items.TANNABLE_HIDES, 1));
        if (BuiltInRegistries.ITEM.getTag(ModTags.Items.LARGE_TANNABLE_HIDES).map(tag -> tag.size() > 0).orElse(false)) {
            registry.addRecipe(new TanningEmiRecipe(category, "large_hide_leather", ModTags.Items.LARGE_TANNABLE_HIDES, TanningRackBlock.LARGE_HIDE_LEATHER));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(hide, 0, 4);
        widgets.addTexture(EmiTexture.PLUS, 21, 8);
        widgets.addSlot(agent, 34, 4);
        widgets.addFillingArrow(58, 5, TanningRackBlock.TANNING_TICKS * 50)
                .tooltipText(List.of(Component.translatable("emi.spicedcider.tanning.time", TanningRackBlock.TANNING_TICKS / 1200)));
        widgets.addSlot(outputs.getFirst(), WIDTH - 26, 0).large(true).recipeContext(this);
    }
}
