package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.mixing.CauldronMixingRecipe;
import com.evandev.spicedcider.recipe.AnvilSmithingRecipe;
import com.evandev.spicedcider.recipe.LabelRecipe;
import com.evandev.spicedcider.recipe.PocketJukeboxDyeRecipe;
import com.evandev.spicedcider.recipe.RenameRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SpicedCider.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RenameRecipe>> RENAME_ITEM = RECIPE_SERIALIZERS.register("rename_item", () -> RenameRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LabelRecipe>> LABEL_ITEM = RECIPE_SERIALIZERS.register("label_item", () -> LabelRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PocketJukeboxDyeRecipe>> POCKET_JUKEBOX_DYE = RECIPE_SERIALIZERS.register("pocket_jukebox_dye", () -> PocketJukeboxDyeRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CauldronMixingRecipe>> CAULDRON_MIXING = RECIPE_SERIALIZERS.register("cauldron_mixing", () -> CauldronMixingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilSmithingRecipe>> ANVIL_SMITHING = RECIPE_SERIALIZERS.register("anvil_smithing", () -> AnvilSmithingRecipe.SERIALIZER);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CauldronMixingRecipe>> CAULDRON_MIXING_TYPE = RECIPE_TYPES.register("cauldron_mixing", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cauldron_mixing")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<AnvilSmithingRecipe>> ANVIL_SMITHING_TYPE = RECIPE_TYPES.register("anvil_smithing", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "anvil_smithing")));
}
