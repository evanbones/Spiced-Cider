package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.recipe.PocketJukeboxDyeRecipe;
import com.evandev.spicedcider.recipe.RenameRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SpicedCider.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RenameRecipe>> RENAME_ITEM = RECIPE_SERIALIZERS.register("rename_item", () -> RenameRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PocketJukeboxDyeRecipe>> POCKET_JUKEBOX_DYE = RECIPE_SERIALIZERS.register("pocket_jukebox_dye", () -> PocketJukeboxDyeRecipe.SERIALIZER);
}