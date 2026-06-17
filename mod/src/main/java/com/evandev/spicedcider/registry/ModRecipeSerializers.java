package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.recipe.RenameRecipe;
import com.evandev.spicedcider.recipe.WorkstoneRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SpicedCider.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WorkstoneRecipe>> WORKSTONE = RECIPE_SERIALIZERS.register("workstone", WorkstoneRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RenameRecipe>> RENAME_ITEM = RECIPE_SERIALIZERS.register("rename_item", () -> RenameRecipe.SERIALIZER);
}