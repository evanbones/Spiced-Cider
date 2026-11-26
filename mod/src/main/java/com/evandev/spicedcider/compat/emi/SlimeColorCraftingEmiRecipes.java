package com.evandev.spicedcider.compat.emi;

import cc.cassian.slime.api.SlimeColor;
import cc.cassian.slime.recipe.SlimeShapedRecipe;
import cc.cassian.slime.registry.SlimeBlocks;
import cc.cassian.slime.registry.SlimeDataComponents;
import cc.cassian.slime.registry.SlimeItems;
import cc.cassian.slime.tags.SlimeItemTags;
import cc.cassian.slime.util.SlimeHelpers;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public final class SlimeColorCraftingEmiRecipes {

    public static void register(EmiRegistry registry) {
        Comparison byColor = Comparison.compareData(stack -> stack.getItemStack().get(SlimeDataComponents.DYED_COLOR));
        registry.setDefaultComparison(Items.SLIME_BALL, byColor);
        registry.setDefaultComparison(SlimeItems.SLIME_SLING, byColor);
        registry.setDefaultComparison(SlimeItems.SLIME_BOOTS, byColor);
        for (RecipeHolder<CraftingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            if (!(holder.value() instanceof SlimeShapedRecipe recipe)) continue;
            if (recipe.result.is(SlimeItems.SLIME_BOOTS)) continue;
            ResourceLocation id = holder.id();
            registry.addRecipe(new EmiCraftingRecipe(grid(recipe, null), EmiStack.of(recipe.result.copy()),
                    ResourceLocation.fromNamespaceAndPath("spicedcider", "/slime_crafting/" + id.getNamespace() + "/" + id.getPath()), false));
            for (SlimeColor color : SlimeColor.values()) {
                if (SlimeColor.isDefault(color)) continue;
                registry.addRecipe(new EmiCraftingRecipe(grid(recipe, color), EmiStack.of(SlimeHelpers.dye(recipe.result.copy(), color)),
                        ResourceLocation.fromNamespaceAndPath("spicedcider", "/slime_crafting/" + id.getNamespace() + "/" + id.getPath() + "/" + color.getSerializedName()), false));
            }
        }
    }

    private static List<EmiIngredient> grid(SlimeShapedRecipe recipe, SlimeColor color) {
        List<Ingredient> ingredients = recipe.getIngredients();
        int width = recipe.getWidth();
        int height = recipe.getHeight();
        List<EmiIngredient> inputs = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row < height && col < width) {
                    Ingredient ingredient = ingredients.get(row * width + col);
                    inputs.add(color == null ? EmiIngredient.of(ingredient) : colored(ingredient, color));
                } else {
                    inputs.add(EmiStack.EMPTY);
                }
            }
        }
        return inputs;
    }

    private static EmiIngredient colored(Ingredient ingredient, SlimeColor color) {
        if (ingredient.isEmpty()) return EmiStack.EMPTY;
        List<EmiStack> stacks = new ArrayList<>();
        boolean slimeBlock = false;
        for (ItemStack stack : ingredient.getItems()) {
            if (stack.is(SlimeItemTags.DYEABLE_SLIME)) {
                stacks.add(EmiStack.of(SlimeHelpers.dye(stack, color)));
            } else if (isSlimeBlock(stack)) {
                slimeBlock = true;
            }
        }
        if (slimeBlock) stacks.add(EmiStack.of(SlimeHelpers.dye(Items.SLIME_BLOCK.getDefaultInstance(), color)));
        return stacks.isEmpty() ? EmiIngredient.of(ingredient) : EmiIngredient.of(stacks);
    }

    private static boolean isSlimeBlock(ItemStack stack) {
        if (stack.is(Items.SLIME_BLOCK) || stack.is(SlimeItemTags.SLIME_BLOCKS)) return true;
        return stack.getItem() instanceof BlockItem blockItem && SlimeBlocks.SLIME_BLOCKS.containsValue(blockItem.getBlock());
    }
}
