package com.evandev.spicedcider.content.haunting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class SoulHaunting {
    public static final int CAMPFIRE_TIME = 200;
    public static final int FIRE_TIME = 100;
    private static final ResourceLocation HAUNTING = ResourceLocation.fromNamespaceAndPath("create", "haunting");

    public static boolean isSoulCampfire(BlockState state) {
        return state.is(Blocks.SOUL_CAMPFIRE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional<ItemStack> result(Level level, ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        RecipeType<?> type = BuiltInRegistries.RECIPE_TYPE.get(HAUNTING);
        if (type == null) return Optional.empty();
        SingleRecipeInput input = new SingleRecipeInput(stack);
        for (Object entry : level.getRecipeManager().getAllRecipesFor((RecipeType) type)) {
            Recipe recipe = ((RecipeHolder<?>) entry).value();
            try {
                if (recipe.matches(input, level)) {
                    ItemStack output = recipe.getResultItem(level.registryAccess());
                    if (!output.isEmpty()) return Optional.of(output.copy());
                }
            } catch (ClassCastException ignored) {
            }
        }
        return Optional.empty();
    }

    public static Optional<RecipeHolder<CampfireCookingRecipe>> campfireRecipe(Level level, ItemStack stack) {
        return result(level, stack).map(output -> new RecipeHolder<>(
                ResourceLocation.fromNamespaceAndPath("spicedcider", "soul_haunting/" + BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath()),
                new CampfireCookingRecipe("", CookingBookCategory.MISC, Ingredient.of(stack.getItem()), output, 0.0F, CAMPFIRE_TIME)));
    }
}
